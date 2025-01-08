package net.engineeringdigest.journalApp.controller;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {
    @Autowired
    private JournalEntryService journalEntryService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<JournalEntry> all = journalEntryService.getAll();
        if(all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }else{
            return new ResponseEntity<JournalEntry>( HttpStatus.NOT_FOUND);
        }
//
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry){
        try {
            myEntry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry(myEntry);
            return new ResponseEntity<JournalEntry>(myEntry, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<JournalEntry>( HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId){
        try {
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
            return journalEntry.map(entry -> new ResponseEntity<JournalEntry>(entry, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }catch (Exception e){
            return new ResponseEntity<JournalEntry>( HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId){
        journalEntryService.deleteById(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("id/{myId}")
    public ResponseEntity<?> updateJournalEntryById(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry){
       JournalEntry oldRecord = journalEntryService.findById(myId).orElse(null);
       if(oldRecord !=null){
            oldRecord.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().equals("")? newEntry.getTitle() : oldRecord.getTitle());
            oldRecord.setContent(newEntry.getContent() != null && !newEntry.getContent().equals("")? newEntry.getContent() : oldRecord.getContent());
           journalEntryService.saveEntry(oldRecord);
           return new ResponseEntity<>(oldRecord, HttpStatus.OK);
       }else{
           return new ResponseEntity<JournalEntry>( HttpStatus.NOT_FOUND);
       }

    }
}
