/* Implementacni soubor pro funkce s prefixem list* */

#include <stdlib.h>
#include <assert.h>
#include "instruction_list.h"

void listInit(struct instructionList* container) {
        struct instruction* zar = malloc(sizeof(struct instruction));
        assert(zar != NULL);
        zar->prev = NULL;
        zar->next = NULL;
        zar->type = 0;
        zar->arg = 0;
        container->current = zar;
        container->end = container->current;
}

unsigned int listClear(struct instructionList* container){
    unsigned int count = 0;
    while (container->end->prev != NULL){
        struct instruction *tmp = container->end->prev;
        container->end->prev = container->end->prev->prev;
        if (container->end->prev != NULL){
            container->end->prev->next = container->end;
        }
        free(tmp);
        count++;
    }
    free(container->end);
    container->end = container->current = NULL;
    return count;
}

/*unsigned int listClear(struct instructionList* container){
    unsigned int count = 0;
    struct instruction * curr = container->end->prev;
    while (curr){
        struct instruction * tmp = curr;
        curr = curr->prev;
        free(tmp);
        count++;
    }
    free(container->end);
    container->end = container->current = NULL;
    return count;
}*/

void listPush(struct instructionList* container, struct instruction* item){
    if (container->current == container->end){
        item->prev = container->end->prev;
        container->end->prev = item;
        item->next = container->end;
        container->current = item;
    }
    else{
        item->next = container->end;
        item->prev = container->end->prev;
        container->end->prev->next = item;
        container->end->prev = item;
    }
}

const struct instruction * listStep(struct instructionList* container){
    if (container->current->next == NULL){
        return NULL;
    }
    else{
    container->current = container->current->next;
    return container->current->prev;}
}

const struct instruction * listBackstep(struct instructionList* container){
    if (container->current->prev == NULL){
        return NULL;
    }
    container->current = container->current->prev;
    return container->current->next;
}

int listEmpty(const struct instructionList* container){
    if (container->end->prev == NULL){
        return 1;
    }
    return 0;
}
