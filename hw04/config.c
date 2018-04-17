#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>
#include <string.h>
#include "config.h"
//#include "log.h"

static char *readLine(FILE *file)
{
    size_t buffer_size = 8;
    char * buffer = malloc(buffer_size * sizeof(char));
    if (buffer == NULL){
        free(buffer);
        return "failed to allocate the buffer";
    }

    char letter = fgetc(file);
    if (letter == EOF){
        free(buffer);
        return NULL;
    }
    size_t buffer_position = 0;

    while (letter != '\n' && letter != EOF) {

        if (buffer_position == buffer_size-1) {
            buffer_size *= 2;
            buffer = realloc(buffer, buffer_size);
            if (buffer == NULL){
                return "failed to allocate the buffer";
            }
        }

        buffer[buffer_position] = letter;
        buffer_position++;
        letter = fgetc(file);
    }
    buffer[buffer_position] = '\0';
    return buffer;
}

int configRead(struct config *cfg, const char *name)
{
    //INICIALIZACIA CFG SECTIONS
    cfg->sections = malloc(4 * sizeof(struct section));
    if (cfg->sections == NULL){
        return 1;
    }
    cfg->sections_count = 0;
    cfg->size = 4;

    cfg->sections->name = NULL;
    cfg->sections->keys = NULL;
    cfg->sections->keys_count = 0;
    cfg->sections->size = 0;



    //OTVORENIE SUBORU
    FILE* file = fopen(name, "r");
    if (file == NULL){
        return 1;
    }

    char *line = NULL;
    while ((line = readLine(file)) != NULL){
        if (strcmp(line, "failed to allocate the buffer") == 0){
            if (line) free(line);
            fclose(file);
            return 1;
        }
        int i = 0;

        if (line[i] == ';'){    //komentar
            free(line);
            continue;
        }
        while (line[i] == ' ' || line[i] == '\t'){ //medzery od zaciatku
            i++;
            }
        if (line[i] == '\0'){  //prazdny riadok
            free(line);
            continue;
        }
        //SEKCIA
        if (line[i] == '['){ //ak sme nasli novu sekciu
            if (i > 0 || line[strlen(line) - 1] != ']'){     //ak je pred alebo za sekciou medzera
                free(line);

                fclose(file);
                //LOG(LError, "error in section format");
                return 2;
            }
            cfg->sections_count++;          //ak sme nasli hranatu zatvorku tak zvysime pocet sekcii
            if (cfg->size <= cfg->sections_count){ //pozrie ci mame dost alokovane
                cfg->size *= 2;                     //ak nie tak zvysi size na dvojnasobok
                cfg->sections = realloc(cfg->sections, cfg->size * sizeof(struct section));
                if (cfg->sections == NULL){
                    free(line);
                    fclose(file);
                    return 1;
                }
            }
            int j = 1;
            int k = strlen(line) - 2;
            while (line[k] == ' ' || line[k] == '\t'){ //najdeme koniec nazvu sekcie
                k--;
            }
            while (line[j] == ' ' || line[j] == '\t'){ //najdeme zaciatok
                j++;
            }
            if (k < j){
                free(line);
                fclose(file);
                return 2;
            }
            int l = 0;
            cfg->sections[cfg->sections_count - 1].name = malloc((k - j + 2) * sizeof(char)); //alokujeme nazov sekcie
            if (cfg->sections[cfg->sections_count - 1].name == NULL){
                free(line);
                fclose(file);
                return 1;
            }
            for (int m = j; m <= k; m++){
                if (isalnum(line[m]) || line[m] == '-' || line[m] == '_' || line[m] == ':'){
                cfg->sections[cfg->sections_count - 1].name[l] = line[m];
                l++;
                }
                else{
                    free(line);
                    fclose(file);
                    //LOG(LError, "invalid section char");
                    return 2;
                }
            }
            //OVERENIE DUPLICITNEJ SEKCIE
            for (size_t i = 0; i < cfg->sections_count - 1; i++){
                if (strcmp(cfg->sections[i].name, cfg->sections[cfg->sections_count - 1].name) == 0){
                    free(line);
                    fclose(file);
                    return 2;
                }
            }
            cfg->sections[cfg->sections_count - 1].name[l] = '\0';
            cfg->sections[cfg->sections_count - 1].keys_count = 0;
            cfg->sections[cfg->sections_count - 1].keys = malloc(4 * sizeof(struct keyval));
            if (cfg->sections[cfg->sections_count - 1].keys == NULL){
                free(line);
                fclose(file);
                return 1;
            }
            cfg->sections[cfg->sections_count - 1].size = 4;
            //LOG(LError, "section name: %s", cfg->sections[cfg->sections_count - 1].name);
            //LOG(LError, "first section key name: %s", conf->sections[0].keys->key);
            free(line);
            continue;
        }

        else {
            if (cfg->sections_count == 0 || line[i] == '=' || (i > 0 && line[i] == ';')){ //ak nemame zatial ziadnu sekciu ale najde neprazdny riadok alebo =
                free(line);
                fclose(file);
                return 2;
            }
            else{
                //KLUC
                cfg->sections[cfg->sections_count - 1].keys_count++;
                char delim[2] = "=";
                char* keyname;

                char line_cpy[strlen(line)]; //kopia line ktoru moze strtok modifikovat
                strcpy(line_cpy, line);

                keyname = strtok(line_cpy, delim);
                if (strcmp(line_cpy, line) == 0){
                    free(line);
                    fclose(file);
                    return 2;
                }
                int namelen = strlen(keyname);
                char *value = keyname + namelen + 1;
                int j = 0;
                int k = strlen(keyname) - 1;
                size_t s_index = cfg->sections_count - 1;
                size_t k_index = cfg->sections[s_index].keys_count - 1; //-1 pretoze indexujeme od nuly
                cfg->sections[s_index].keys[k_index].key = NULL;
                cfg->sections[s_index].keys[k_index].val = NULL;
                cfg->sections[s_index].keys[k_index].val_int = -12345;


                if (cfg->sections[s_index].size <= cfg->sections[s_index].keys_count){ //pozrie ci mame dost alokovane
                    cfg->sections[s_index].size *= 2;                     //ak nie tak zvysi size na dvojnasobok
                    cfg->sections[s_index].keys = realloc(cfg->sections[s_index].keys, cfg->sections[s_index].size * sizeof(struct keyval));
                }

                while (keyname[k] == ' ' || keyname[k] == '\t'){ //najdeme koniec nazvu sekcie
                    k--;
                }
                while (keyname[j] == ' ' || keyname[j] == '\t'){ //najdeme zaciatok
                    j++;
                }
                cfg->sections[s_index].keys[k_index].key = malloc((k - j + 2) * sizeof(char)); //alokujeme nazov key
                if (cfg->sections[s_index].keys[k_index].key == NULL){
                    free(line);
                    fclose(file);
                    return 1;
                }
                int l = 0;
                for (int m = j; m <= k; m++){
                    if (isalnum(keyname[m])){
                    cfg->sections[s_index].keys[k_index].key[l] = keyname[m];
                    l++;
                    }
                    else{
                        //LOG(LError, "invalid key");
                        cfg->sections[s_index].keys[k_index].key[l] = '\0';
                        free(line);
                        fclose(file);
                        return 2;
                    }
                }
                    cfg->sections[s_index].keys[k_index].key[l] = '\0'; //na koniec nazvu kluca dame koncovu nulu
                    //LOG(LInfo, "%d. key in section _%s_ _%s_ ", k_index, cfg->sections[s_index].name, cfg->sections[s_index].keys[k_index].key);

                    //OVERENIE DUPLICITNEHO KLUCA
                    for (size_t g = 0; g < cfg->sections[s_index].keys_count - 1; g++){
                        if (strcmp(cfg->sections[s_index].keys[g].key, cfg->sections[s_index].keys[cfg->sections[s_index].keys_count - 1].key) == 0){
                            free(line);
                            fclose(file);
                            return 2;
                        }
                    }

                    //HODNOTA
                    j = 0;
                    k = strlen(value) - 1;
                    while (value[k] == ' ' || value[k] == '\t'){ //najdeme koniec nazvu sekcie
                        k--;
                        //printf("koniec: %d\n", k);
                    }
                    while (value[j] == ' ' || value[j] == '\t'){ //najdeme zaciatok
                        j++;
                    }
                    l = 0;
                    if (k < j){
                        cfg->sections[s_index].keys[k_index].val = malloc(sizeof(char));
                        if (cfg->sections[s_index].keys[k_index].val == NULL){
                            free(line);
                            fclose(file);
                            return 1;
                        }
                    }
                    else {
                        cfg->sections[s_index].keys[k_index].val = malloc((k - j + 2) * sizeof(char)); //alokujeme hodnotu
                        if (cfg->sections[s_index].keys[k_index].val == NULL){
                            free(line);
                            fclose(file);
                            return 1;
                        }
                        for (int m = j; m <= k; m++){
                            cfg->sections[s_index].keys[k_index].val[l] = value[m];
                            l++;
                        }
                    }
                    cfg->sections[s_index].keys[k_index].val[l] = '\0';

                    int is_int = 0;
                    for (size_t k = 0; k < strlen(cfg->sections[s_index].keys[k_index].val); k++){ //overime ci tam nie je int
                        if (isdigit(cfg->sections[s_index].keys[k_index].val[k]) != 0 ||
                                (((cfg->sections[s_index].keys[k_index].val[k]) == '-') && (k == 0))){
                            continue;
                        }
                        else{
                            is_int = 1;
                            break;
                        }
                    }
                    if (is_int == 0){
                        cfg->sections[s_index].keys[k_index].val_int = atoi(cfg->sections[s_index].keys[k_index].val);
                        //LOG(LInfo, "val_int in key %s is %d", cfg->sections[s_index].keys[k_index].key, cfg->sections[s_index].keys[k_index].val_int);

                    }
                    if (strcmp(cfg->sections[s_index].keys[k_index].val, "1") == 0 ||
                        strcmp(cfg->sections[s_index].keys[k_index].val, "true") == 0 ||
                        strcmp(cfg->sections[s_index].keys[k_index].val, "yes") == 0){
                        cfg->sections[s_index].keys[k_index].val_int = 1;

                    }
                    else if(strcmp(cfg->sections[s_index].keys[k_index].val, "0") == 0 ||
                            strcmp(cfg->sections[s_index].keys[k_index].val, "false") == 0 ||
                            strcmp(cfg->sections[s_index].keys[k_index].val, "no") == 0){
                        cfg->sections[s_index].keys[k_index].val_int = 0;

                    }
                    //LOG(LInfo, "section %d: %s key: %s", cfg->sections_count, cfg->sections[s_index].name, cfg->sections[s_index].keys[k_index].key);
                    //LOG(LInfo, "value: %s", cfg->sections[s_index].keys[k_index].val);
            }
        }
        if (line) free(line);
    }
    fclose(file);
    return 0;
}


int configValue(const struct config *cfg,
                const char *section,
                const char *key,
                enum configValueType type,
                void *value)
{
    for (size_t i = 0; i < cfg->sections_count; i++){
        if (strcmp(cfg->sections[i].name, section) == 0){ //sekcia sa nasla
            for (size_t j = 0; j < cfg->sections[i].keys_count; j++){
                if (strcmp(cfg->sections[i].keys[j].key, key) == 0){ //kluc sa nasiel
                    if (type == CfgString){
                        *((const char**)value) = cfg->sections[i].keys[j].val;
                        return 0;
                    }
                    if (type == CfgInteger){
                        if (cfg->sections[i].keys[j].val_int == -12345){
                            return 3; //neda sa pretypovat
                        }
                        *((int *)value) = cfg->sections[i].keys[j].val_int;
                        return 0;
                    }
                    if (type == CfgBool){
                        if (cfg->sections[i].keys[j].val_int != 0 && cfg->sections[i].keys[j].val_int != 1){
                            return 3; //neda sa pretypovat
                        }
                        *((int *)value) = cfg->sections[i].keys[j].val_int;
                        return 0;
                    }
                    return 4;
                }
            }
            return 2; //kluc sa nenasiel (skoncil for)
       }
    }
    return 1; //sekcia sa nenasla

}


void configClean(struct config *cfg) //zeby som opravil aj potencial leaks?
{
    if (cfg->sections_count == 0 && (cfg->sections)){
        free(cfg->sections);
        return;
    }
    for (size_t i = 0; i < cfg->sections_count; i++){
        for (size_t j = 0; j < cfg->sections[i].keys_count; j++){
            if (j < cfg->sections[i].keys_count){
                free(cfg->sections[i].keys[j].key);
                free(cfg->sections[i].keys[j].val);
            }
        }
        if (cfg->sections[i].keys) free(cfg->sections[i].keys);
        if (cfg->sections[i].name) free(cfg->sections[i].name);
    }
    if (cfg->sections) free(cfg->sections);
    cfg->sections = NULL;
    cfg->sections_count = 0;
}
