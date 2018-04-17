cmake_minimum_required(VERSION 3.5)
project(hw03)


set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=c99 -g -Wall -Wextra -pedantic")

set(HW03_SOURCE cpu.c instruction_list.c main.c )
add_executable(hw03 ${HW03_SOURCE})
