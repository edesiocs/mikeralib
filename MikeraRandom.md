# Introduction #

The mikeralib random librray contains a fast pseudorandom number generator and a host of useful random number utility functions.

The internal random number generator uses the "XORShift" algorithm

# Details #

Example usage:

```
// Create a random 32-bit int
int number=Rand.nextInt;

// Create a random String (helpful for test functions)
String s=Rand.nextString;

// Roll two six sided dice and return the sum
int dice=Rand.d(2,6);

// Create a random integer in the range 0 <=x< 20
int index=Rand.r(20);

```