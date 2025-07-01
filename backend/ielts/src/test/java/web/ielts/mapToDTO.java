package web.ielts;

import org.junit.jupiter.api.Test;
import web.ielts.Tips.dto.TipDTO;
import web.ielts.Tips.model.ListeningTips;
import web.ielts.Tips.model.ReadingTips;
import web.ielts.Tips.model.SpeakingTips;
import web.ielts.Tips.model.WritingTips;
import web.ielts.Tips.service.TipsService;

import static org.junit.jupiter.api.Assertions.*;

public class mapToDTO {

    private final TipsService tipMapperService = new TipsService();

    /**
     * Testcase TC01:
     * Chức năng: Kiểm tra ánh xạ từ ListeningTips sang TipDTO
     * Câu lệnh được thực thi:
     *  - if (tip instanceof ListeningTips) → true
     * Nhánh được phủ: Nhánh if đầu tiên
     */
    @Test
    void testMapToDTO_WithListeningTips() {
        ListeningTips tip = new ListeningTips("1", "Listening", "Type1", "Description1");
        TipDTO result = tipMapperService.mapToDTO(tip);
        assertEquals(tip.getId(), result.getId());
        assertEquals(tip.getSkill(), result.getSkill());
        assertEquals(tip.getType(), result.getType());
        assertEquals(tip.getDescription(), result.getDescription());
    }

    /**
     * Testcase TC02:
     * Chức năng: Kiểm tra ánh xạ từ SpeakingTips sang TipDTO
     * Câu lệnh được thực thi:
     *  - if (tip instanceof ListeningTips) → false
     *  - else if (tip instanceof SpeakingTips) → true
     * Nhánh được phủ: Nhánh else if SpeakingTips
     */
    @Test
    void testMapToDTO_WithSpeakingTips() {
        SpeakingTips tip = new SpeakingTips("2", "Speaking", "Type2", "Description2");
        TipDTO result = tipMapperService.mapToDTO(tip);
        assertEquals(tip.getId(), result.getId());
    }

    /**
     * Testcase TC03:
     * Chức năng: Kiểm tra ánh xạ từ ReadingTips sang TipDTO
     * Câu lệnh được thực thi:
     *  - if (tip instanceof ListeningTips) → false
     *  - else if (tip instanceof SpeakingTips) → false
     *  - else if (tip instanceof ReadingTips) → true
     * Nhánh được phủ: Nhánh else if ReadingTips
     */
    @Test
    void testMapToDTO_WithReadingTips() {
        ReadingTips tip = new ReadingTips("3", "Reading", "Type3", "Description3");
        TipDTO result = tipMapperService.mapToDTO(tip);
        assertEquals(tip.getId(), result.getId());
    }

    /**
     * Testcase TC04:
     * Chức năng: Kiểm tra ánh xạ từ WritingTips sang TipDTO
     * Câu lệnh được thực thi:
     *  - if (tip instanceof ListeningTips) → false
     *  - else if (tip instanceof SpeakingTips) → false
     *  - else if (tip instanceof ReadingTips) → false
     *  - else if (tip instanceof WritingTips) → true
     * Nhánh được phủ: Nhánh else if WritingTips
     */
    @Test
    void testMapToDTO_WithWritingTips() {
        WritingTips tip = new WritingTips("4", "Writing", "Type4", "Description4");
        TipDTO result = tipMapperService.mapToDTO(tip);
        assertEquals(tip.getId(), result.getId());
    }

    /**
     * Testcase TC05:
     * Chức năng: Kiểm tra khi truyền kiểu không hợp lệ vào mapToDTO
     * Câu lệnh được thực thi:
     *  - if (tip instanceof ListeningTips) → false
     *  - else if (tip instanceof SpeakingTips) → false
     *  - else if (tip instanceof ReadingTips) → false
     *  - else if (tip instanceof WritingTips) → false
     *  - else → true (ném exception)
     * Nhánh được phủ: Nhánh else
     */
    @Test
    void testMapToDTO_WithUnsupportedType_ShouldThrowException() {
        Object unsupportedTip = new Object();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tipMapperService.mapToDTO(unsupportedTip);
        });
        assertTrue(exception.getMessage().contains("Unsupported tip type"));
    }
}
