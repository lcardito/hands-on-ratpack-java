package me.lcardito.ratpack;

import java.util.Optional;
import java.util.function.Function;

/**
 * ```
 * Does JDK8's Optional class satisfy the Monad laws?
 * =================================================
 * 1.  Left identity:  true
 * 2.  Right identity: true
 * 3.  Associativity:  true
 * <p>
 * Yes, it does.
 * ```
 * <p>
 * To install the JDK8 Early Access release via Ubuntu PPA, see:
 * http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html
 * <p>
 * For more info on the monad laws, see:
 * [1] http://learnyouahaskell.com/a-fistful-of-monads#monad-laws
 * [2] http://eed3si9n.com/learning-scalaz/Monad+laws.html
 * [3] http://en.wikipedia.org/wiki/Monad_(functional_programming)#Monad_laws
 * <p>
 * NOTE: Code below does *not* use lambdas, because the mainline Java 8 Early
 * Access builds installed by the PPA do not yet include lambda expressions.
 *
 * @author Marc Siegel <marc.siegel@timgroup.com>
 */

class MonadLaws {

	public static void main(String[] args) throws java.lang.Exception {
		System.out.println("");
		System.out.println("Does JDK8's Optional class satisfy the Monad laws?");
		System.out.println("=================================================");
		System.out.println("  1.  Left identity:  " + satisfiesLaw1LeftIdentity());
		System.out.println("  2.  Right identity: " + satisfiesLaw2RightIdentity());
		System.out.println("  3.  Associativity:  " + satisfiesLaw3Associativity());
		System.out.println("");
		System.out.println(satisfiesLaw1LeftIdentity()
			&& satisfiesLaw2RightIdentity()
			&& satisfiesLaw3Associativity()
			? "Yes, it does."
			: "No, it doesn't.");
	}

	// Input values for the monad law tests below
	private static Integer value = 42;
	private static Optional<Integer> monadicValue = Optional.of(value);

	// With lambdas, this entire thing goes away (pass `Optional.of` directly)
	private static Function optionalOf = (Function<Integer, Optional<Integer>>) Optional::of;

	// With lambdas, this becomes `n -> Optional.of(n * 2)`
	private static Function f = (Function<Integer, Optional<Integer>>) n -> Optional.of(n * 2);

	// With lambdas, this becomes `n -> Optional.of(n * 5)`
	private static Function g = (Function<Integer, Optional<Integer>>) n -> Optional.of(n * 5);

	// NOTE (2013-11-11): Bug in latest JDK8 requires this cast:  ^^^^^^^^^^^^^^^^^^^
	// With lambdas, this becomes `n -> f(n).flatMap(g)`
	private static Function f_flatMap_g = (Function<Integer, Optional<Integer>>) n -> ((Optional<Integer>) f.apply(n)).flatMap(g);


	/**
	 * Monad law 1, Left Identity
	 * <p>
	 * From LYAHFGG [1] above:
	 * The first monad law states that if we take a value, put it in a default context
	 * with return and then feed it to a function by using >>=, it’s the same as just
	 * taking the value and applying the function to it
	 */
	private static boolean satisfiesLaw1LeftIdentity() {
		return Optional.of(value).flatMap(f).equals(f.apply(value));
	}

	/**
	 * Monad law 2, Right Identity
	 * <p>
	 * From LYAHFGG [1] above:
	 * The second law states that if we have a monadic value and we use >>= to feed
	 * it to return, the result is our original monadic value.
	 */
	private static boolean satisfiesLaw2RightIdentity() {
		return monadicValue.flatMap(optionalOf).equals(monadicValue);
	}

	/**
	 * Monad law 3, Associativity
	 * <p>
	 * From LYAHFGG [1] above:
	 * The final monad law says that when we have a chain of monadic function
	 * applications with >>=, it shouldn’t matter how they’re nested.
	 */
	private static boolean satisfiesLaw3Associativity() {
		return monadicValue.flatMap(f).flatMap(g).equals(monadicValue.flatMap(f_flatMap_g));
	}
}
