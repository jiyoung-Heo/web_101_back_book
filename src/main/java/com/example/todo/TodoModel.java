package com.example.todo;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

//롬복이 성공적으로 적용되는지 확인
@Builder
@RequiredArgsConstructor
public class TodoModel {

	@NonNull
	private String id;
}