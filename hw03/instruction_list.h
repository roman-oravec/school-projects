/*
   Hlavickovy soubor pro funkce s prefixem list*,
   mimo deklarace techto zde i definujte struktury
   instruction, instructionList a typ instructionType.
   Includujte prave a pouze hlavickove soubory primo potrebne touto hlavickou.

   Toto je dobre misto, kde s implementaci zacit.
   Treba zkopirovanim prototypu funkci ze zadani.
 */

#include <stdint.h>

enum instructionType {InstNop = 0, InstAdd, InstSub, InstDiv,
                      InstMul, InstMova, InstLoad, InstSwac,
                      InstSwab, InstInc, InstDec, InstPush,
                      InstPop};

//possible types of instructions

struct instruction {struct instruction* prev;
                    struct instruction* next;
                    enum instructionType type;
                    int32_t arg;};

struct instructionList {struct instruction* current;
                        struct instruction* end;};
//linked list of instructions

void listInit(struct instructionList* container);

//initialize list

unsigned int listClear(struct instructionList* container);

//clear dat list

void listPush(struct instructionList* container, struct instruction* item);

//push new inst to the list

const struct instruction * listStep(struct instructionList* container);

//move current pointer to the right and return pointer to inst

const struct instruction * listBackstep(struct instructionList* container);

//same as listStep but we move current to the left

int listEmpty(const struct instructionList* container);

//determine if the list is empty
