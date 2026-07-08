/**
 * Chain of Responsibility: pass a request along a chain of handlers until one
 * of them handles it, so the sender doesn't need to know which handler will.
 * Real Java examples: Servlet FilterChain, Spring Security's filter chain —
 * each filter decides to act and/or forward to the next one in line.
 * Run:  java 11-design-patterns/ChainOfResponsibility.java
 */
public class ChainOfResponsibility {

    abstract static class Approver {
        private Approver next;

        Approver setNext(Approver next) {
            this.next = next;
            return next; // lets callers chain setNext calls fluently
        }

        final void handle(double amount) {
            if (canApprove(amount)) {
                System.out.println("  [" + getClass().getSimpleName() + "] approved $" + amount);
            } else if (next != null) {
                System.out.println("  [" + getClass().getSimpleName() + "] can't approve $" + amount + ", forwarding");
                next.handle(amount);
            } else {
                System.out.println("  [" + getClass().getSimpleName() + "] no one left to approve $" + amount);
            }
        }

        abstract boolean canApprove(double amount);
    }

    static class TeamLead extends Approver {
        @Override
        boolean canApprove(double amount) { return amount <= 1_000; }
    }

    static class Manager extends Approver {
        @Override
        boolean canApprove(double amount) { return amount <= 10_000; }
    }

    static class Director extends Approver {
        @Override
        boolean canApprove(double amount) { return amount <= 100_000; }
    }

    public static void main(String[] args) {
        System.out.println("== chain of responsibility ==");

        Approver teamLead = new TeamLead();
        teamLead.setNext(new Manager()).setNext(new Director());

        teamLead.handle(500);       // handled by TeamLead
        teamLead.handle(5_000);     // forwarded to Manager
        teamLead.handle(50_000);    // forwarded to Director
        teamLead.handle(500_000);   // falls off the end of the chain
    }
}
