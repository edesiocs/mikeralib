# Introduction #

Mikeralib contains a set of persistent data structures. These are immutable, thread safe data structures supporting efficient update operations through structural sharing.

These are partly inspired by the persistent data structures used in Clojure.

# Details #


I'm particularly pleased with my PersistentHashMap implementation. Features I have been targeting:
  * Supports all the read-only operations from the Java Collections Framework (java.util.Map<K,V> etc.)
  * Supports persistent updates with structural sharing
  * Comparable in performance to java.util.HashMap  (in the 1-2x range)
  * Obviously has the benefits of being fully immutable and thread-safe

Here are a few things that I did to optimise performance which seemed to help noticeably in my benchmarks:
  * Internal structure nodes use inheritance rather than interfaces - interface dispatch was measurably costly on my setup (JDK 1.6)
  * Custom Map.Entry iterator that understands the data structure
  * Custom lightweight wrappers for Map.entrySet(), Map.keySet() and Map.values()
  * A special "null" root node with a shared singleton instance that efficiently implements empty maps
  * Made the branching factor tunable (2,4,8,16,32). Currently 32 seems to work best overall :-) but it was worth checking and it does vary based on workload