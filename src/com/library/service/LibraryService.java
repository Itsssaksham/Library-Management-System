package com.library.service;
import com.library.exception.*; import com.library.model.*; import com.library.util.CSVUtil;
import java.io.IOException; import java.time.LocalDate; import java.util.*; import java.util.concurrent.atomic.AtomicLong; import java.util.stream.Collectors;
public class LibraryService {
    private final Map<Long, Book> books = new HashMap<>(); private final Map<Long, Member> members = new HashMap<>(); private final Map<Long, Transaction> transactions = new HashMap<>();
    private final AtomicLong bookIdGen = new AtomicLong(0); private final AtomicLong memberIdGen = new AtomicLong(0); private final AtomicLong transIdGen = new AtomicLong(0);
    private final String booksFile; private final String membersFile; private final String transFile;
    public LibraryService(String dataDir) { this.booksFile = dataDir + "/books.csv"; this.membersFile = dataDir + "/members.csv"; this.transFile = dataDir + "/transactions.csv"; loadFromFiles(); }
    private void loadFromFiles() {
        try {
            List<String[]> b = CSVUtil.readAll(booksFile);
            for (String[] row : b) { try { long id = Long.parseLong(row[0]); Book bk = new Book(id, row[1], row[2], row[3], Integer.parseInt(row[4]), Integer.parseInt(row[5])); books.put(id, bk); bookIdGen.updateAndGet(x -> Math.max(x, id)); } catch (Exception ex){} }
            List<String[]> m = CSVUtil.readAll(membersFile);
            for (String[] row : m) { try { long id = Long.parseLong(row[0]); Member mm = new Member(id, row[1], row[2], row[3]); members.put(id, mm); memberIdGen.updateAndGet(x -> Math.max(x, id)); } catch (Exception ex){} }
            List<String[]> t = CSVUtil.readAll(transFile);
            for (String[] row : t) { try { long id = Long.parseLong(row[0]); long bookId = Long.parseLong(row[1]); long memberId = Long.parseLong(row[2]); LocalDate issue = LocalDate.parse(row[3]); LocalDate due = LocalDate.parse(row[4]); LocalDate ret = (row.length>5 && !row[5].isEmpty()) ? LocalDate.parse(row[5]) : null; Transaction tr = new Transaction(id, bookId, memberId, issue, due, ret); transactions.put(id, tr); transIdGen.updateAndGet(x -> Math.max(x, id)); } catch (Exception ex){} }
        } catch (IOException e) { System.err.println("Warning: could not load files: " + e.getMessage()); }
    }
    public synchronized void saveAll() throws IOException {
        List<String> bLines = books.values().stream().sorted(Comparator.comparingLong(Book::getId)).map(Book::toString).collect(Collectors.toList());
        CSVUtil.writeAll(booksFile, bLines);
        List<String> mLines = members.values().stream().sorted(Comparator.comparingLong(Member::getId)).map(Member::toString).collect(Collectors.toList());
        CSVUtil.writeAll(membersFile, mLines);
        List<String> tLines = transactions.values().stream().sorted(Comparator.comparingLong(Transaction::getId)).map(Transaction::toString).collect(Collectors.toList());
        CSVUtil.writeAll(transFile, tLines);
    }
    public synchronized Book addBook(String title, String author, String category, int copies) { long id = bookIdGen.incrementAndGet(); Book b = new Book(id, title, author, category, copies, copies); books.put(id, b); return b; }
    public synchronized void removeBook(long bookId) throws BookNotFoundException { if (!books.containsKey(bookId)) throw new BookNotFoundException("Book not found: " + bookId); books.remove(bookId); }
    public synchronized Book getBook(long bookId) throws BookNotFoundException { Book b = books.get(bookId); if (b == null) throw new BookNotFoundException("Book not found: " + bookId); return b; }
    public synchronized List<Book> searchBooksByTitle(String q) { String low = q.toLowerCase(); return books.values().stream().filter(b -> b.getTitle().toLowerCase().contains(low)).collect(Collectors.toList()); }
    public synchronized Member registerMember(String name, String email, String phone) { long id = memberIdGen.incrementAndGet(); Member m = new Member(id, name, email, phone); members.put(id, m); return m; }
    public synchronized Member getMember(long memberId) throws MemberNotFoundException { Member m = members.get(memberId); if (m == null) throw new MemberNotFoundException("Member not found: " + memberId); return m; }
    public synchronized Transaction issueBook(long bookId, long memberId, int daysToReturn) throws BookNotFoundException, MemberNotFoundException, InsufficientCopiesException { Book book = getBook(bookId); Member member = getMember(memberId); if (book.getAvailableCopies() <= 0) throw new InsufficientCopiesException("No copies available for book " + book.getTitle()); book.setAvailableCopies(book.getAvailableCopies() - 1); long id = transIdGen.incrementAndGet(); LocalDate issue = LocalDate.now(); LocalDate due = issue.plusDays(daysToReturn); Transaction tr = new Transaction(id, bookId, memberId, issue, due, null); transactions.put(id, tr); return tr; }
    public synchronized Transaction returnBook(long transactionId) throws Exception { Transaction tr = transactions.get(transactionId); if (tr == null) throw new Exception("Transaction not found: " + transactionId); if (tr.getReturnDate() != null) throw new Exception("Book already returned for transaction: " + transactionId); tr.setReturnDate(LocalDate.now()); Book b = getBook(tr.getBookId()); b.setAvailableCopies(b.getAvailableCopies() + 1); return tr; }
    public synchronized List<Transaction> listCurrentIssued() { return transactions.values().stream().filter(t -> t.getReturnDate() == null).collect(Collectors.toList()); }
    public synchronized List<Transaction> listOverdue() { return transactions.values().stream().filter(Transaction::isOverdue).collect(Collectors.toList()); }
    public synchronized List<Transaction> historyForMember(long memberId) { return transactions.values().stream().filter(t -> t.getMemberId() == memberId).collect(Collectors.toList()); }
    public synchronized Map<String, Long> countByCategory() { return books.values().stream().collect(Collectors.groupingBy(Book::getCategory, Collectors.counting())); }
    public synchronized Collection<Book> listBooks() { return books.values(); } public synchronized Collection<Member> listMembers() { return members.values(); } public synchronized Collection<Transaction> listTransactions() { return transactions.values(); }
}
