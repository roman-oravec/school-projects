/*
   Hlavickovy soubor pro funkce s prefixem stack* a cpu*,
   mimo deklarace techto zde i definujte struktury stack a cpu.
   Includujte prave a pouze hlavickove soubory primo potrebne touto hlavickou.
 */

#include <stdint.h>
#include "instruction_list.h"

struct stack {int32_t* values; int32_t* top;};

//stack of integers with 2048 KiB allocated

void stackInit(struct stack* stack);

//initialize the stack

void stackClear(struct stack* stack);

//clear the stack

void stackPush(struct stack* stack, int32_t cpu_register);

//push value from register A to the stack

void stackPop(struct stack* stack);

//pop value from the top of the stack

struct cpu {int32_t registers[3]; struct instructionList programList; struct stack memory;};

//structure representing out cpu

void cpuInit (struct cpu* cpu);

//initialize the cpu

void cpuClear (struct cpu* cpu);

//set registers to 0 and free the memory used by cpu

int cpuStep(struct cpu* cpu);

//do current instruction

void cpuDebug(const struct cpu* cpu);

//print registers and stack
