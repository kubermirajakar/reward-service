package com.kubertech.rewardsystem.utility;

/**
 * Utility class for calculating reward points based on transaction amounts.
 * <p>
 * This class provides static helper methods and is not meant to be instantiated.
 * Reward logic follows a tiered approach:
 * <ul>
 *     <li>No points for amounts ≤ 50</li>
 *     <li>1 point per dollar between 51 and 100</li>
 *     <li>2 points per dollar for amounts over 100</li>
 * </ul>
 */
public class RewardPointsUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * <p>
     * Throws an {@link UnsupportedOperationException} if called.
     */
    private RewardPointsUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Calculates reward points earned based on the given transaction amount.
     *
     * @param amount the total value of the transaction
     * @return the calculated reward points
     */
    public static int calculateRewardPoints(double amount) {
        if (amount <= 50) {
            return 0;
        }
        if (amount <= 100) {
            return (int) (amount - 50);
        }
        return 50 + 2 * (int) (amount - 100);
    }
}