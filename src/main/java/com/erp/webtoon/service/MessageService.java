package com.erp.webtoon.service;

import com.erp.webtoon.domain.Message;
import com.erp.webtoon.domain.User;
import com.erp.webtoon.domain.Webtoon;
import com.erp.webtoon.dto.message.FeedbackListDto;
import com.erp.webtoon.dto.message.MessageListDto;
import com.erp.webtoon.dto.message.MessageRequestDto;
import com.erp.webtoon.dto.message.MessageUpdateDto;
import com.erp.webtoon.repository.MessageRepository;
import com.erp.webtoon.repository.WebtoonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final WebtoonRepository webtoonRepository;
    private final SlackService slackService;

    /*
        메시지 조회
        - msgType == all
        - msgType == deptCode
        - rcvUser == emp_id
    */
    @Transactional(readOnly = true)
    public List<MessageListDto> findMessageList(User user) {
        List<Message> messageList = messageRepository.findByMsgTypeOrMsgTypeOrRcvUser("all", user.getDeptCode(), user);

        return messageList.stream()
                .map(message -> MessageListDto.builder()
                        .content(message.getContent())
                        .refId(message.getRefId())
                        .program(message.getProgram())
                        .sendUser(message.getSendUser())
                        .build())
                .collect(Collectors.toList());
    }

    /*
        메시지 상태 변경
        - 수신 -> Y
        - 읽음 -> R
        - 삭제 -> N
    */
    public void modifyStat(MessageUpdateDto dto) {
        Message message = dto.toEntity();
        char stat = message.getStat();
        message.changeStat(stat);
    }

    /*
        메시지 등록
    */
    public void addMsg(MessageRequestDto dto) throws IOException {

        Message message = dto.toEntity();
        messageRepository.save(message);

        if (message.getMsgType().equals("dm")) {
            // dm일 경우 수신자의 사번을 전달
            slackService.sendSlackChannel(message.getContent(), message.getRcvUser().getEmployeeId());
        }
        else {
            slackService.sendSlackChannel(message.getContent(), message.getMsgType());
        }
    }

    /*
        피드백 조회
        - msgType : webtoon
        - 수신자 : null
    */
    @Transactional(readOnly = true)
    public List<FeedbackListDto> findFeedbackList(Long webtoonId) {
        List<Message> feedbackList = messageRepository.findByRefId(webtoonId);

        return feedbackList.stream()
                .map(feedback -> FeedbackListDto.builder()
                        .content(feedback.getContent())
                        .sendUser(feedback.getSendUser())
                        .build())
                .collect(Collectors.toList());
    }

    /*
        피드백 등록
        - msgType : webtoon
        - 수신자 : null
    */
    public void addFeedbackMsg(MessageRequestDto dto) throws IOException {

        //피드백 저장
        Message feedbackMsg = dto.toEntity();
        messageRepository.save(feedbackMsg);

        //메시지 저장
        Webtoon webtoon = webtoonRepository.findById(feedbackMsg.getRefId())
                .orElseThrow(() -> new EntityNotFoundException("웹툰 정보가 존재하지 않습니다."));

        String originContent = feedbackMsg.getContent();
        dto.setContent(webtoon.getTitle() + "에 피드백이 등록되었습니다. \n\n" + originContent);
        addMsg(dto);

    }


}