package ru.practicum.participationrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateRequestsResponse {
    private List<RequestDetailsDto> confirmedRequests;
    private List<RequestDetailsDto> rejectedRequests;
}
