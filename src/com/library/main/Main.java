package com.library.main;
import com.library.exception.*; import com.library.model.*; import com.library.service.LibraryService;
import java.io.IOException; import java.util.List; import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        final LibraryService svc = new LibraryService("data");
        Thread overdueNotifier = new Thread(() -> {
            while (true) {
                try {
                    List<Transaction> overdue = svc.listOverdue();
                    if (!overdue.isEmpty()) {
                        System.out.println("=== Overdue Notice ===");
                        overdue.forEach(t -> System.out.println("Transaction " + t.getId() + " is overdue. BookId=" + t.getBookId() + ", MemberId=" + t.getMemberId()));
                    }
                    Thread.sleep(30_000);
                } catch (InterruptedException e) { break; } catch (Exception e) { e.printStackTrace(); }
            }
        }, "OverdueNotifier"); overdueNotifier.setDaemon(true); overdueNotifier.start();
        Thread autosave = new Thread(() -> {
            while (true) {
                try { Thread.sleep(60_000); svc.saveAll(); System.out.println("[AutoSave] Saved data to CSV."); } catch (InterruptedException e) { break; } catch (IOException e) { System.err.println("[AutoSave] Error saving: " + e.getMessage()); }
            }
        }, "AutoSave"); autosave.setDaemon(true); autosave.start();
        Scanner sc = new Scanner(System.in); boolean exit = false; printHelp();
        while (!exit) {
            System.out.print("\n> "); String cmd = sc.nextLine().trim();
            try {
                if (cmd.equalsIgnoreCase("help")) { printHelp(); }
                else if (cmd.equalsIgnoreCase("list books")) { svc.listBooks().forEach(b -> System.out.println(b.getId() + ": " + b.getTitle() + " (" + b.getAvailableCopies() + "/" + b.getTotalCopies() + ")")); }
                else if (cmd.startsWith("add book")) { String[] p = cmd.split("\\|"); if (p.length < 5) { System.out.println("Usage: add book|title|author|category|copies"); continue; } Book b = svc.addBook(p[1].trim(), p[2].trim(), p[3].trim(), Integer.parseInt(p[4].trim())); System.out.println("Added book: " + b.getId()); }
                else if (cmd.startsWith("search ")) { String q = cmd.substring(7).trim(); svc.searchBooksByTitle(q).forEach(b -> System.out.println(b.getId()+": "+b.getTitle())); }
                else if (cmd.startsWith("register ")) { String[] p = cmd.split("\\|"); if (p.length < 4) { System.out.println("Usage: register|name|email|phone"); continue;} System.out.println("Member id: " + svc.registerMember(p[1].trim(), p[2].trim(), p[3].trim()).getId()); }
                else if (cmd.startsWith("issue ")) { String[] p = cmd.split("\\|"); if (p.length < 4) { System.out.println("Usage: issue|bookId|memberId|days"); continue;} long bookId = Long.parseLong(p[1].trim()); long memberId = Long.parseLong(p[2].trim()); int days = Integer.parseInt(p[3].trim()); var tr = svc.issueBook(bookId, memberId, days); System.out.println("Issued txn id: " + tr.getId()); }
                else if (cmd.startsWith("return ")) { String[] p = cmd.split("\\|"); if (p.length < 2) { System.out.println("Usage: return|transactionId"); continue; } long tid = Long.parseLong(p[1].trim()); svc.returnBook(tid); System.out.println("Returned transaction " + tid); }
                else if (cmd.equalsIgnoreCase("list issued")) { svc.listCurrentIssued().forEach(t -> System.out.println(t.getId() + ": book=" + t.getBookId() + ", member=" + t.getMemberId() + ", due=" + t.getDueDate())); }
                else if (cmd.equalsIgnoreCase("list overdue")) { svc.listOverdue().forEach(t -> System.out.println(t.getId() + ": book=" + t.getBookId() + ", member=" + t.getMemberId() + ", due=" + t.getDueDate())); }
                else if (cmd.equalsIgnoreCase("save")) { svc.saveAll(); System.out.println("Saved."); }
                else if (cmd.equalsIgnoreCase("exit")) { System.out.println("Exiting. Saving..."); svc.saveAll(); exit = true; }
                else { System.out.println("Unknown command. Type 'help' for commands list."); }
            } catch (Exception ex) { System.out.println("Error: " + ex.getMessage()); }
        }
        sc.close(); System.exit(0);
    }
    private static void printHelp() {
        System.out.println("Library CLI commands:"); System.out.println(" help"); System.out.println(" list books"); System.out.println(" add book|title|author|category|copies"); System.out.println(" search <query>"); System.out.println(" register|name|email|phone"); System.out.println(" issue|bookId|memberId|days"); System.out.println(" return|transactionId"); System.out.println(" list issued"); System.out.println(" list overdue"); System.out.println(" save"); System.out.println(" exit"); }}
