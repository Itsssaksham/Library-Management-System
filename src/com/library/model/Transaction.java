package com.library.model;
import java.time.LocalDate; import java.time.format.DateTimeFormatter;
public class Transaction {
    private long id; private long bookId; private long memberId;
    private LocalDate issueDate; private LocalDate dueDate; private LocalDate returnDate;
    private static final DateTimeFormatter F = DateTimeFormatter.ISO_LOCAL_DATE;
    public Transaction() {}
    public Transaction(long id, long bookId, long memberId, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate) {
        this.id=id; this.bookId=bookId; this.memberId=memberId; this.issueDate=issueDate; this.dueDate=dueDate; this.returnDate=returnDate;
    }
    public long getId(){return id;} public void setId(long id){this.id=id;}
    public long getBookId(){return bookId;} public void setBookId(long bookId){this.bookId=bookId;}
    public long getMemberId(){return memberId;} public void setMemberId(long memberId){this.memberId=memberId;}
    public LocalDate getIssueDate(){return issueDate;} public void setIssueDate(LocalDate issueDate){this.issueDate=issueDate;}
    public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate dueDate){this.dueDate=dueDate;}
    public LocalDate getReturnDate(){return returnDate;} public void setReturnDate(LocalDate returnDate){this.returnDate=returnDate;}
    @Override public String toString(){ String ret = (returnDate==null)?"":returnDate.format(F); return id+","+bookId+","+memberId+","+issueDate.format(F)+","+dueDate.format(F)+","+ret; }
    public boolean isOverdue(){ return returnDate==null && LocalDate.now().isAfter(dueDate); }
}
