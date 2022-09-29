# CS5700 Programming Assignment 1
Author: Haotian Shen

# How to run my program
The program will take a file as input and output a file containing all the answers for the input expressions.
Run the program in intelliJ.
Server command line arguments:
```12345```


Client comamnd line arguments:
```localhost 12345 /Users/haotianshen/Desktop/CS5700PA1/src/main/input/input.txt /Users/haotianshen/Desktop/CS5700PA1/src/main/output/output.txt```

Start the server before starting the client. The server is multi-threaded and can handle multiple client requests simultaneously.
## Sample input/output
input:
```
5
3
1+1
4
12+1
7
325+325
6
100-81
3
9-3
```
output:
```
5
1
2
2
13
3
650
2
19
1
6
```



