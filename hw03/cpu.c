/* Implementacni soubor pro funkce s prefixem stack* a cpu* */

#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include "cpu.h"

void stackInit(struct stack* stack){
    stack->values = malloc(2048);
    assert(stack->values);
    stack->top = NULL;
}

void stackClear(struct stack* stack){
    if (stack->values){
    free(stack->values);
    stack->top = stack->values = NULL;}
}

void stackPush(struct stack* stack, int32_t cpu_register){
    if (stack->top == NULL){
        *(stack->values) = cpu_register;
        stack->top = stack->values;
    }
    else{
    stack->top++;
    *(stack->top) = cpu_register;
    }
}

void stackPop(struct stack* stack) {
    if (stack->top > stack->values){
        stack->top--;
    }
    else{
        stack->top = NULL;
    }
}

void cpuInit(struct cpu* cpu){
    stackInit(&(cpu->memory));
    listInit(&(cpu->programList));
    for (int i = 0; i<3; i++){
        cpu->registers[i] = 0;
    }
}

void cpuClear(struct cpu* cpu){
    for (int i = 0; i<3; i++){
        cpu->registers[i] = 0;
    }
    listClear(&(cpu->programList));
    stackClear(&(cpu->memory));
}

int cpuStep(struct cpu* cpu){
    const struct instruction * inst;
    inst = listStep(&(cpu->programList));
    switch (inst->type) {
    case InstNop:
        break;
    case InstAdd:
        cpu->registers[0] += cpu->registers[2];
        break;
    case InstSub:
        cpu->registers[0] -= cpu->registers[2];
        break;
    case InstDiv:
        assert(cpu->registers[2] != 0);
        cpu->registers[0] /= cpu->registers[2];
        break;
    case InstMul:
        cpu->registers[0] *= cpu->registers[2];
        break;
    case InstMova:
        cpu->registers[0] = inst->arg;
        break;
    case InstLoad:
        assert(&(cpu->memory.top[-cpu->registers[1]]) >= cpu->memory.values);
        if (cpu->registers[1] >= 0){
        cpu->registers[0] = cpu->memory.top[-cpu->registers[1]];}
        else{
            cpu->registers[0] = *cpu->memory.top;
        }
        break;
    case InstSwab:
        cpu->registers[0] += cpu->registers[1];
        cpu->registers[1] = cpu->registers[0] - cpu->registers[1];
        cpu->registers[0] -= cpu->registers[1];
        break;
    case InstSwac:
        cpu->registers[0] += cpu->registers[2];
        cpu->registers[2] = cpu->registers[0] - cpu->registers[2];
        cpu->registers[0] -= cpu->registers[2];
        break;
    case InstInc:
        cpu->registers[0]++;
        break;
    case InstDec:
        cpu->registers[0]--;
        break;
    case InstPush:
        stackPush(&(cpu->memory), cpu->registers[0]);
        break;
    case InstPop:
        stackPop(&(cpu->memory));
        break;
    default:
        return 0;
    }
    return 1;
}

void cpuDebug(const struct cpu* cpu){
    printf("# Registers %d %d %d | Stack", cpu->registers[0],
            cpu->registers[1], cpu->registers[2]);
    for (int32_t * val = cpu->memory.values; val <= cpu->memory.top; val++){
        printf(" %d", *val);
    }
    printf("\n");
}
