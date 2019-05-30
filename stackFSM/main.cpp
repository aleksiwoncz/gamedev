#include <functional>
#include <iostream>
#include <cmath>
#include <thread>
#include <chrono>
#include <stack>

using namespace std;

struct Vector2D {
    float x, y;

    Vector2D(float x, float y) {
        this->x = x;
        this->y = y;
    }

    Vector2D& operator +=( const Vector2D& v ) {
        this->x += v.x;
        this->y += v.y;
        return *this;
    }
};

Vector2D *user = new Vector2D(80, 90);
Vector2D *queen = new Vector2D(40, 60);
Vector2D *home = new Vector2D(0, 0);
Vector2D *leafPosition = new Vector2D(100, 100);

float distance(Vector2D* v1, Vector2D *v2) {
    return sqrt(pow(v1->x - v2->x, 2) + pow(v1->y - v2->y, 2));
}

template<typename T, typename... U>
long getAddress(std::function<T(U...)> f) {
    return *(long *)(char *)&f;
}

class FSM {
private:
    stack<function<void ()>> statesStack;

public:
    FSM() {}

    function<void ()> popState() {
        function<void ()> result = this->statesStack.top();
        this->statesStack.pop();
        return result;
    }

    void pushState(function<void ()> state) {
        function<void ()> activeState = getCurrentState();
        if(activeState == NULL)
            this->statesStack.push(state);
        else if(getAddress(activeState) == getAddress(state))
            this->statesStack.push(state);
    }

    function<void ()> getCurrentState(){
        return this->statesStack.size() > 0 ? this->statesStack.top() : NULL;
    }

    void update() {
        function<void ()> activeState = getCurrentState();
        if(activeState != NULL) activeState();
    }
};



class Ant {
public:
    Vector2D *position;
    Vector2D *velocity;
    FSM *brain;

    Ant(float pos_x, float pos_y) {
        this->position = new Vector2D(pos_x, pos_y);
        this->velocity = new Vector2D(-1, -1);
        this->brain = new FSM();
        this->brain->pushState(bind(&Ant::findLeaf,this));
    }

    void findLeaf();
    void goHome();
    void runAway();
    void meetQueen();

    void move() {
        *this->position += *this->velocity;
    }

    void update() {
        brain->update();
        this->move();
    }
};

void Ant::findLeaf() {
    cout << "Szukam liscia. Moja pozycja: " << this->position->x << " " << this->position->y << endl;
    this->velocity = new Vector2D(leafPosition->x - position->x, leafPosition->y - position->y);


    if(distance(leafPosition, this->position) <= 10) {
        cout << "Hurra! Mam lisc!" << endl;
        this->brain->popState();
        this->brain->pushState(bind(&Ant::goHome,this));
    }

    if(distance(queen, this->position) <= 30) this->brain->pushState(bind(&Ant::meetQueen,this));

    if(distance(user, this->position) <= 120) this->brain->pushState(bind(&Ant::runAway,this));
}

void Ant::goHome() {
    cout << "Wracam do domu. Moja pozycja: " << this->position->x << " " << this->position->y << endl;
    this->velocity = new Vector2D(home->x - position->x, home->y - position->y);

    if(distance(home, this->position) <= 10) {
        this->brain->popState();
        this->brain->pushState(bind(&Ant::findLeaf,this));
    }

    if(distance(queen, this->position) <= 30) this->brain->pushState(bind(&Ant::meetQueen,this));

    if(distance(user, this->position) <= 120) this->brain->pushState(bind(&Ant::runAway,this));

}

void Ant::runAway() {
    cout << "Uciekam! Czyjas syra! Moja pozycja: " << this->position->x << " " << this->position->y << endl;
    this->velocity = new Vector2D(position->x - user->x, position->y - user->y);
    if(distance(user, this->position) > 120) {
        this->brain->popState();
    }
}

void Ant:: meetQueen() {
    cout << "Krolowa jest w poblizu, musze sie przywitac!" << endl;
    this->velocity = new Vector2D(position->x - queen->x, position->y - queen->y);
    if(distance(queen, this->position) > 30) {
        this->brain->popState();
    }
}

int main() {
    Ant *obj = new Ant(40, 70);
    while(true) {
        this_thread::sleep_for(chrono::milliseconds(1000));
        obj->update();
    }

    return 0;
}
