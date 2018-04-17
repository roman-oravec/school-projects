/*
  Implementacni soubor pro obsluhu programu.
  Tady reste cteni standardniho vstupu, aj.
*/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "cpu.h"

static struct instruction* instMake(enum instructionType type){ //allocate instruction, set type
    struct instruction* ins = malloc(sizeof(struct instruction));
    ins->prev = ins->next = NULL;
    ins->type = type;
    ins->arg = 0;
    return ins;
}

static void input(int n){               //get input, push instructions, execute commands
    char* command = calloc(16, sizeof(char));
    struct cpu* proc = malloc(sizeof(struct cpu));
    cpuInit(proc);
    while (strncmp(command, "halt", 4) != 0){
        printf("> ");
        fgets(command, 16, stdin);
        if (strncmp(command, "nop", 3) == 0){
            listPush(&(proc->programList), instMake(InstNop));
        }
        else if (strncmp(command, "add", 3) == 0){
            listPush(&(proc->programList), instMake(InstAdd));
        }
        else if (strncmp(command, "sub", 3) == 0){
            listPush(&(proc->programList), instMake(InstSub));
        }
        else if (strncmp(command, "div", 3) == 0){
            listPush(&(proc->programList), instMake(InstDiv));
        }
        else if (strncmp(command, "mul", 3) == 0){
            listPush(&(proc->programList), instMake(InstMul));
        }
        else if (strncmp(command, "mova", 4) == 0){
            struct instruction * ins = instMake(InstMova);
            ins->arg = atoi(command + 4*sizeof(char));
            listPush(&(proc->programList), ins);
        }
        else if (strncmp(command, "load", 4) == 0){
            listPush(&(proc->programList), instMake(InstLoad));
        }
        else if (strncmp(command, "swab", 4) == 0){
            listPush(&(proc->programList), instMake(InstSwab));
        }
        else if (strncmp(command, "swac", 4) == 0){
            listPush(&(proc->programList), instMake(InstSwac));
        }
        else if (strncmp(command, "inc", 3) == 0){
            listPush(&(proc->programList), instMake(InstInc));
        }
        else if (strncmp(command, "dec", 3) == 0){
            listPush(&(proc->programList), instMake(InstDec));
        }
        else if (strncmp(command, "push", 4) == 0){
            listPush(&(proc->programList), instMake(InstPush));
        }
        else if (strncmp(command, "pop", 3) == 0){
            listPush(&(proc->programList), instMake(InstPop));
        }
        else if (strncmp(command, "run", 3) == 0 && n == 0){
            while (proc->programList.current != proc->programList.end){
                cpuStep(proc);
            }
            cpuDebug(proc);
        }
        else if (strncmp(command, "run", 3) == 0 && n != 0){
            for (int i = 0; i < n; i++){
                if (proc->programList.current == proc->programList.end){
                    break;
                }
                cpuStep(proc);
            }
            cpuDebug(proc);

        }
    }
    free(command);
    cpuClear(proc);
    free(proc);
}

int main(int argc, char *argv[]){
    assert(argc > 1);
    switch (argv[1][1]){
    case 'h':
        printf("Program akceptuje nasledujici prepinace:\n"
               "-h          : program vypise informace o programu a jeho prepinacich, "
               "nasledne se ukonci\n"
               "-r <number> : po zavolani prikazu run program vyhodnoti nejvyse <number> "
               "instrukci\n"
               "-R          : po zavolani prikazu run program vyhodnocuje instrukce ve tak dlouho,"
               " dokud nenarazi na instrukci halt, nebo nevycerpa soupisku instrukci");
        break;

    case 'r':
        input(atoi(argv[2]));
        break;
    case 'R':
        input(0);
        break;
    }
}
