package net.engineeringdigest.journalApp.controller;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {
    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping("{userName}")
    public ResponseEntity<?> getAllJournalEntriesOfUser(@PathVariable String userName) {
        User user = userService.findByUserName(userName);
        List<JournalEntry> all = user.getJournalEntries();
        if(all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }else{
            return new ResponseEntity<JournalEntry>( HttpStatus.NOT_FOUND);
        }
//
    }

    @PostMapping("{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String userName){
        try {

            journalEntryService.saveEntry(myEntry, userName);
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

    @DeleteMapping("id/{userName}/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId, @PathVariable String userName){
        journalEntryService.deleteById(myId, userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("id/{userName}/{myId}")
    public ResponseEntity<?> updateJournalEntryById(
            @PathVariable ObjectId myId,
            @RequestBody JournalEntry newEntry,
            @PathVariable String userName
    ){
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
