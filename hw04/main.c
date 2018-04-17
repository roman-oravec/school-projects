#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>

#include "log.h"
#include "config.h"
#include "module-cache.h"
#include "module-toupper.h"
#include "module-tolower.h"
#include "module-decorate.h"
#include "module-magic.h"


void process(const char *queryText, struct module *modules, int modulesCount, struct module *post, int postCount) {

    struct query query;
    memset(&query, 0, sizeof(struct query));
    query.query = queryText;
    query.queryCleanup = NULL;
    query.response = "";
    query.responseCleanup = NULL;

    LOG(LInfo, "query: %s", queryText);

    int m = 0;
    char *status = "";
    for (; m < modulesCount; ++m) {
        LOG(LDebug, "Running module %s", modules[m].name);
        modules[m].process(&modules[m], &query);

        switch (query.responseCode) {
        case RCSuccess:
            LOG(LInfo, "Response success");
            status = "SUCCESS";
            continue;
        case RCDone:
            LOG(LInfo, "Response done");
            status = "DONE";
            break;
        case RCError:
            status = "ERROR";
            break;
        default:
            LOG(LError, "Error");
            status = "UNKNOWN";
            break;
        }
        break;
    }
    LOG(LDebug, "responseCode: %i", query.responseCode);
    if (query.responseCode == RCSuccess) {
        m = -1;
        while (++m < postCount) {
            if (post[m].postProcess) {
                LOG(LDebug, "Postprocessing by %s", post[m].name);
                post[m].postProcess(&post[m], &query);
            }
        }
    }
    LOG(LInfo, "response: %s", query.response);
    printf("query: %s\nresponse: %s\nstatus: %s\n", query.query, query.response, status);

    if (query.responseCleanup) {
        query.responseCleanup(&query);
    }
    if (query.queryCleanup) {
        query.queryCleanup(&query);
    }
}

void setLogSetting(const struct config *cfg)
{
    const char *logFile;
    if (!configValue(cfg, "log", "File", CfgString, &logFile)) {
        if (setLogFile(logFile)) {
            LOG(LWarn, "Invalid value for File: '%s'", logFile);
        }
    }
    const char *logLevel;
    if (!configValue(cfg, "log", "Level", CfgString, &logLevel)) {
        if (!strcmp(logLevel, "D") || !strcmp(logLevel, "d")) {
            setLogLevel(LDebug);
        } else if (!strcmp(logLevel, "I") || !strcmp(logLevel, "i")) {
            setLogLevel(LInfo);
        } else if (!strcmp(logLevel, "W") || !strcmp(logLevel, "w")) {
            setLogLevel(LWarn);
        } else if (!strcmp(logLevel, "E") || !strcmp(logLevel, "e")) {
            setLogLevel(LError);
        } else if (!strcmp(logLevel, "F") || !strcmp(logLevel, "f")) {
            setLogLevel(LFatal);
        } else if (!strcmp(logLevel, "N") || !strcmp(logLevel, "n")) {
            setLogLevel(LNoLog);
        } else {
            LOG(LWarn, "Invalid value for Mask: '%s'", logLevel);
        }
    }
}

int loadConfig(const char *configFile,
               struct module *modules,
               struct module **selected,
               int modulesCount,
               int *sel_count,
               struct module **post,
               int *post_count)
{
    struct config cfg;
    int rv = configRead(&cfg, configFile);
    switch (rv) {
    case 0:
        break;
    case 1:
        LOG(LError, "Config file '%s' cannot be opened", configFile);
        break;
    case 2:
        LOG(LError, "Config file '%s' is corrupted", configFile);
        break;
    }

    setLogSetting(&cfg);  //nastavenie logovania

    //MOJE
    char* processModules; //string so vsetkymi modulmi v process
    if ((rv = (configValue(&cfg, "run", "Process", CfgString, &processModules)))){ //ak sme nenasli [run] alebo Process
        LOG(LError, "Cannot setup run plan");
        configClean(&cfg);
        return 3;
    }
    //PARSOVANIE VALUE
    size_t splitSize = 4;
    size_t m_index = 0;
    char **splitModules = malloc(splitSize *sizeof(char*)); //nezabudni free
    const char s[2] = " ";
    char *token;
    token = strtok(processModules, s);

    while( token != NULL ){
        if (splitSize <= m_index){
            splitSize *= 2;
            splitModules = realloc(splitModules, splitSize * sizeof(char*));
        }
        splitModules[m_index] = token;
        m_index++;
        token = strtok(NULL, s);
    }
    //KONIEC PARSOVANIA VALUE

    //NACITANIE SELECTED MODULES
    size_t sel_size = 4;
    size_t sel_index = 0;
    *selected = malloc(sel_size * sizeof(struct module)); //inicializuj v maine a predaj pointer

    for (size_t i = 0; i < m_index; i++){
        int found = 0;
        for (int j = 0; j < modulesCount; j++){

            if (strcmp(modules[j].name, splitModules[i]) == 0){               

                //REALOKACIA
                if (sel_size <= sel_index){
                    sel_size *= 2;
                    *selected = realloc(*selected, sel_size * sizeof(struct module));
                    if (*selected == NULL){
                        return 2;
                    }
                }

                //PRIRADENIE
                *(*selected + sel_index) = modules[j];
                sel_index++;
                found = 1;
                break;
            }
        }
        if (found == 0){
            LOG(LError, "module %s not found", splitModules[i]);
            return 2;
        }
    }
    *sel_count = sel_index; //selectedModulesCount

    //POSTPROCESS
    char* postModules; //string so vsetkymi modulmi v process
    if (!(rv = (configValue(&cfg, "run", "PostProcess", CfgString, &postModules)))){ //ak sme nasli PostProcess
        //PARSOVANIE NA STRINGY
        size_t splitSize = 4;
        size_t m_index = 0;
        char **postSplitModules = malloc(splitSize *sizeof(char*)); //nezabudni free
        const char s[2] = " ";
        char *token;
        token = strtok(postModules, s);

        while( token != NULL ){
            if (splitSize <= m_index){
                splitSize *= 2;
                postSplitModules = realloc(postSplitModules, splitSize * sizeof(char*));
            }
            postSplitModules[m_index] = token;
            m_index++;
            token = strtok(NULL, s);
        }
        //KONIEC PARSOVANIA
        size_t sel_size = 4;
        size_t sel_index = 0;
        *post = malloc(sel_size * sizeof(struct module)); //inicializuj v maine a predaj pointer

        for (size_t i = 0; i < m_index; i++){
            int found = 0;
            for (int j = 0; j < modulesCount; j++){

                if (strcmp(modules[j].name, postSplitModules[i]) == 0){

                    //REALOKACIA
                    if (sel_size <= sel_index){
                        sel_size *= 2;
                        *post = realloc(*post, sel_size * sizeof(struct module));
                        if (*post == NULL){
                            return 2;
                        }
                    }

                    //PRIRADENIE
                    *(*post + sel_index) = modules[j];
                    sel_index++;
                    found = 1;
                    break;
                }
            }
            if (found == 0){
                LOG(LError, "module %s not found", postSplitModules[i]);
                return 2;
            }
        }
        *post_count = sel_index; //selectedModulesCount
        free(postSplitModules);

    }
    //KONIEC MOJICH VECI

    char section[265] = "module::";
    char *moduleName = section + strlen(section);
    for (int m = 0; m < modulesCount; ++m) {
        if (!modules[m].loadConfig) {
            continue;
        }
        strcpy(moduleName, modules[m].name);
        LOG(LDebug, "Loading config of section '%s'", section);
        if ((rv = modules[m].loadConfig(&modules[m], &cfg, section))) {
            LOG(LWarn, "Config loading failed (module: '%s', rv: %i)", modules[m].name, rv);
        }
    }
    free(splitModules);
    configClean(&cfg);
    return 0;
}

void processFile(const char *file,
                 struct module *modules,
                 int modulesCount,
                 struct module *selectedPost,  //pridane mnou
                 int selectedPostCount)         //pridane mnou
{
    LOG(LDebug, "Opening file '%s'", file);
    FILE *input = fopen(file, "r");
    if (!input) {
        LOG(LError, "Cannot open file '%s'", file);
        return;
    }
    char line[64 + 1] = {0};

    for (int i = 1; fgets(line, 64, input); ++i) {
        
        for (char *end = line + strlen(line) - 1; isspace(*end); --end) {
            *end = '\0';
        }

        LOG(LDebug, "line: '%s'", line);
        process(line, modules, modulesCount, selectedPost, selectedPostCount);
    }
    fclose(input);
}


int main(int argc, char **argv)
{
    if (argc < 3) {
        LOG(LError, "Cannot run without config and input file name");
        return 6;
    }

    const char *configFile = argv[1];
    const char *inputFile = argv[2];

    int modulesCount = 5;

    struct module modules[5];
    moduleCache(&modules[0]);
    moduleToUpper(&modules[1]);
    moduleDecorate(&modules[2]);
    moduleToLower(&modules[3]);
    moduleMagic(&modules[4]);


    struct module *selectedModules = NULL; // = malloc(4 * sizeof(struct module));
    struct module *selectedPost = NULL;
    int selectedModulesCount = 0;
    int selectedPostCount = 0;
    int rv;
    if ((rv = loadConfig(configFile, modules, &selectedModules, modulesCount, &selectedModulesCount, &selectedPost, &selectedPostCount))) {
        if (rv == 1)
            LOG(LWarn, "config file %s is missing", configFile);
        else{
            for (int m = 0; m < modulesCount; ++m) {
                if (modules[m].cleanup) {
                    modules[m].cleanup(&modules[m]);
                }
            }
            return rv;
        }
    }
    LOG(LInfo, "Start");
    //for (int i = 0; i < selectedPostCount; i++){
    //    printf("post %s\n", selectedPost[i].name);
    //}

    processFile(inputFile, selectedModules, selectedModulesCount, selectedPost, selectedPostCount);
    /*if (selectedPost){
        processFile(inputFile, selectedPost, selectedPostCount);
    }*/

    for (int m = 0; m < modulesCount; ++m) {
        if (modules[m].cleanup) {
            modules[m].cleanup(&modules[m]);
        }
    }
    free(selectedModules);
    if (selectedPost) free(selectedPost);
    LOG(LInfo, "Finished");
    return 0;
}
