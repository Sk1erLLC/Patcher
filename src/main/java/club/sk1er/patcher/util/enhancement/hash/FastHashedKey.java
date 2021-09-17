package club.sk1er.patcher.util.enhancement.hash;

public class FastHashedKey {
    public static int mix64(long input) {
        input = (input ^ (input >> 30)) * 0xbf58476d1ce4e5b9L;
        input = (input ^ (input >> 27)) * 0x94d049bb133111ebL;
        input = input ^ (input >> 31);
        return Long.hashCode(input);
    }
}
