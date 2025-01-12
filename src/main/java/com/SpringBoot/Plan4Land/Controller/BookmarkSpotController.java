package com.SpringBoot.Plan4Land.Controller;

import com.SpringBoot.Plan4Land.Service.BookMarkSpotService;
import com.SpringBoot.Plan4Land.Service.BookMarkSpotService.SpotDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class BookmarkSpotController {

    private final BookMarkSpotService bookMarkSpotService;

    // 북마크 추가
    @PostMapping("/add")
    public ResponseEntity<String> addBookmark(@RequestParam String memberId, @RequestParam String spotId) {
        String response = bookMarkSpotService.addBookmark(memberId, spotId);
        return ResponseEntity.ok(response);
    }

    // 북마크 삭제
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeBookmark(@RequestParam String memberId, @RequestParam String spotId) {
        String response = bookMarkSpotService.removeBookmark(memberId, spotId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/status")
    public ResponseEntity<Boolean> getBookmarkStatus(@RequestParam String memberId, @RequestParam String spotId) {
        boolean isBookmarked = bookMarkSpotService.isBookmarked(memberId, spotId);
        return ResponseEntity.ok(isBookmarked);
    }
}
