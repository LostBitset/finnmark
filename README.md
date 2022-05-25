# finnmark
When your teacher says you have to use Java....

A simple lisp with a Java interpreter.

I don't have time to add multithreading support, so the interpreter itself utilizes two threads. One is for evaluating the code, the other converts it to bytecode. It's sort of like JIT compilation except instead of seeing some code being used and compiling it *then* using it, it sees code being used and uses it right away, and also compiles it so that if it's accessed again it will be faster. There is a `force-bytecode` special form that allows you to ensure code is either interpreted or compiled. 

Values are represented with the `FVal` class, which has a `dep` attribute, which determines whether or not it depends on it's environment in some way. For example, functions are always `dep`, because they depend on their arguments. When a value is accessed multiple times, one of two things occurs. For `!dep` values, they are simply cached (this is sort of like memoization for `eval`). For `dep` values, they are sent to the compilation thread to be converted to Finnmark bytecode (not JVM bytecode). Both of these cases are represented with the same class (`FValResolved`). This is because cached `!dep` values are also bytecode - just simple bytecode that loads a value. This allows for the bytecode generation process to be implemented recursively and involve `!dep` values that have been cached. Once you enter the territory of `FValResolved`, everything is bytecode.

Importantly, this "bubbles" up. The bytecode generation process starts by converting the innermost parts into bytecode first. Then, the inner bytecode is integrated into outer bytecode. The outer bytecode generation invokes the inner bytecode generation. This makes it so that bytecode never has to call into interpreted code, which is really nice and (I think) avoids a ton of subtle issues. 

