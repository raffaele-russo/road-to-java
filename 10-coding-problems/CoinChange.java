import java.util.Arrays;

/**
 * Coin Change: minimum number of coins to make an amount. The canonical
 * bottom-up dynamic programming problem — dp[i] = fewest coins for amount i.
 * O(amount * coins.length) time, O(amount) space.
 * Run:  java 10-coding-problems/CoinChange.java
 */
public class CoinChange {

    static int minCoins(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;                                    // base case: 0 coins for amount 0
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i && dp[i - coin] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);   // take this coin + best for the rest
                }
            }
        }
        return dp[amount] == Integer.MAX_VALUE ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        int[] coins = {1, 5, 10, 25};
        System.out.println("minCoins(41)  = " + minCoins(coins, 41));  // 25+10+5+1 = 4 coins
        assert minCoins(coins, 41) == 4;

        int[] coins2 = {3, 5};
        System.out.println("minCoins(7,{3,5}) = " + minCoins(coins2, 7)); // impossible
        assert minCoins(coins2, 7) == -1;

        System.out.println("minCoins(0)   = " + minCoins(coins, 0));
        assert minCoins(coins, 0) == 0;

        System.out.println("OK (run with -ea to enable assertions)");
    }
}
