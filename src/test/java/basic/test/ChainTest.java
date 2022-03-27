package basic.test;

public class ChainTest {
    private static class DamageChain {
        DamageChain next;
        DamageChain prev;

        int dmg;

        public DamageChain(int dmg) {
            this.dmg = dmg;
        }

        public int bind() {
            DamageChain chain = this;
            int dmg = chain.dmg;

            while (chain.prev != null) {
                dmg += chain.dmg;
                chain = chain.prev;
            }

            return dmg;
        }
    }

    private static class DamageChainPipeLine {
        DamageChain head;
        DamageChain tail;

        public DamageChainPipeLine() {
            head = new DamageChain(0);
            tail = new DamageChain(0);
            head.next = tail;
            tail.prev = head;
        }

        public void add(DamageChain newCtx) {
            DamageChain nextCtx = head.next;
            newCtx.prev = head;
            newCtx.next = nextCtx;
            head.next = newCtx;
            nextCtx.prev = newCtx;
        }

        public int bind() {
            return tail.bind();
        }
    }

    public static void main(String[] args) {
        DamageChainPipeLine chain = new DamageChainPipeLine();

        chain.add(new DamageChain(1));
        chain.add(new DamageChain(2));
        chain.add(new DamageChain(3));
    }
}
