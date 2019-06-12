#include <iostream>
#include <chrono>
#include <thread>
#include <random>

using namespace std;

class forestFire {
private:
    char **forest;
    char **tempForest;
    int forestSize;
    int probFire, probSelfIgnition, probRebirth;
    int x, y;
    int generationNum;
    unsigned tree;
public:
    forestFire(int size){
        forestSize = size;

        forest = new char* [forestSize + 2];
        for(int i = 0; i <forestSize + 2 ; ++i)
            forest[i] = new char [forestSize + 2];

        for(int i=0; i < forestSize + 1; ++i) {
            for(int j = 0; j < forestSize + 2; ++j){
                forest[i][j] = '.';
            }
        }
        for(int i = 1; i < forestSize + 1; ++i) {
            for(int j = 1; j < forestSize + 1; ++j){
                forest[i][j] = '^';
            }
        }

        tempForest = new char* [forestSize + 2];
        for(int i = 0; i < forestSize + 2 ; ++i)
            tempForest[i] = new char[forestSize + 2];

        tree = chrono::system_clock::now().time_since_epoch().count();
    }

    void positiveAnswer() {
        cout << "Where do you want to put a burning tree? Enter the row and the column: ";
        cin >> x >> y;
        forest[y][x]='*';
    }

    void gainInformation() {
        cout << "\nEnter the probability of firing a tree if the tree next to it is burning [0-100]:  ";
        cin >> probFire;

        cout << "\nEnter the probability of self-ignition of the tree [0-100]: ";
        cin >> probSelfIgnition;

        cout<<"\nEnter the probability of a tree rebirth [0-100]: ";
        cin >> probRebirth;

        cout<<"\nEnter the number of forest generations: ";
        cin >> generationNum;

    }

    void burnIt() {
        minstd_rand0 generator(tree);

        for(int i = 0; i < generationNum; ++i) {
            for(int j = 0;j < forestSize + 2; ++j) {
                for(int k = 0;k < forestSize + 2; ++k) {
                    tempForest[j][k] = forest[j][k];
                }
            }

            for(int i = 1; i < forestSize + 1; ++i) {
                for(int j = 1; j < forestSize + 1; ++j) {
                    cout << tempForest[i][j];
                    switch(tempForest[i][j]) {
                        case '*': {
                            forest[i][j]='.';
                            break;
                        }
                        case '.': {
                            if(tempForest[i - 1][j - 1] == '*' || tempForest[i-1][j] == '*' || tempForest[i - 1][j + 1] == '*')
                                break;

                            else if(tempForest[i][j - 1] == '*'|| tempForest[i][j + 1] == '*')
                                break;

                            else if(tempForest[i + 1][j - 1] == '*' || tempForest[i + 1][j] == '*' || tempForest[i + 1][j + 1] == '*')
                                break;

                            else {
                                if(generator() % 101 < probRebirth)
                                    forest[i][j]='^';
                            }

                            break;
                        }
                        case '^': {
                            if(tempForest[i - 1][j - 1] == '*' || tempForest[i-1][j] == '*' || tempForest[i - 1][j + 1] == '*') {
                                if(generator() % 101 < probFire)
                                    forest[i][j] = '*';
                            }
                            else if(tempForest[i][j - 1] == '*' || tempForest[i][j + 1] == '*') {
                                if(generator() % 101 < probFire)
                                    forest[i][j]='*';
                            }
                            else if(tempForest[i + 1][j - 1] == '*' || tempForest[i + 1][j] == '*' || tempForest[i + 1][j + 1] == '*') {
                                if(generator() % 101 < probFire)
                                    forest[i][j]='*';
                            }
                            else {
                                if(generator() % 101 < probSelfIgnition)
                                    forest[i][j]='*';
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }

                cout << endl;
            }

            cout << "GENERATION " << i + 1 << endl;
            this_thread::sleep_for(chrono::seconds(2));
            system("CLS");

        }
    }
};

int main() {
    cout << "Welcome to the forest fire simulation!"<< endl;
    this_thread::sleep_for(chrono::seconds(2));
    system("CLS");

    cout << "Symbols: ^ is a tree, * is a burning tree, . is a burned tree" << endl;


    cout << "Please, enter the size of your forest [N x N]: ";
    int forestSize;
    cin >> forestSize;
    forestFire* game = new forestFire(forestSize);


    cout<<"\nDo you want to add a burning tree? [1 - yes, 0 - no): ";
    bool answer;
    cin >> answer;

    if(answer)
        game -> positiveAnswer();

    game -> gainInformation();

    system("CLS");

    game -> burnIt();

    return 0;
}
