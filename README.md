# finnmark
When your teacher says you have to use Java....

A simple lisp with a Java interpreter.

I don't have time to add multithreading support, so the interpreter itself utilizes two threads. One is for evaluating the code, the other converts it to bytecode. It's sort of like JIT compilation except instead of seeing some code being used and compiling it *then* using it, it sees code being used and uses it right away, and also compiles it so that if it's accessed again it will be faster. There is a `force-bytecode` special form that allows you to ensure code is either interpreted or compiled. 
