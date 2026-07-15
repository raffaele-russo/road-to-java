public class ProductExceptSelfSolution {
    static int[] productExceptSelf(int[] nums) {
        int[] result = new int[nums.length];
        int prefix = 1;
        for (int i = 0; i < nums.length; i++) { result[i] = prefix; prefix *= nums[i]; }
        int suffix = 1;
        for (int i = nums.length - 1; i >= 0; i--) { result[i] *= suffix; suffix *= nums[i]; }
        return result;
    }
    public static void main(String[] args) {
        assert java.util.Arrays.equals(productExceptSelf(new int[]{1,2,3,4}), new int[]{24,12,8,6});
    }
}
