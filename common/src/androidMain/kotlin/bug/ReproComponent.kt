package bug

import me.tatarka.inject.annotations.Component

// A kotlin-inject @Component so KSP (applied via the convention plugin) actually runs its
// processor and generates code into androidMain, mirroring the real project's KSP usage.
@Component
abstract class ReproComponent
