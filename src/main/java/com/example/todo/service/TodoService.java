package com.example.todo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.todo.model.TodoEntity;
import com.example.todo.persistence.TodoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TodoService {
	@Autowired
	private TodoRepository repository;

	// public String testService() {
	// 	//TodoEntity 생성
	// 	TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
	// 	//TodoEntity 저장
	// 	repository.save(entity);
	// 	//TodoEntity 검색
	// 	TodoEntity savedEntity = repository.findById(entity.getId()).get();
	// 	return savedEntity.getTitle();
	// }

	public List<TodoEntity> create(final TodoEntity entity) {
		//Validations(검증)
		validate(entity);

		repository.save(entity);

		log.info("Entity Id : {} is saved.", entity.getId());

		return repository.findByUserId(entity.getUserId());
	}

	//리팩토링한 메서드
	private void validate(final TodoEntity entity) {
		if (entity == null) {
			log.warn("Entity cannot be null.");
			throw new RuntimeException("Entity cannot be null");
		}
		if (entity.getUserId() == null) {
			log.warn("Unknown user.");
			throw new RuntimeException("Unknown user.");
		}
	}

	public List<TodoEntity> retrieve(final String userId) {
		return repository.findByUserId(userId);
	}

	public List<TodoEntity> update(final TodoEntity entity) {
		//저장할 entity가 유효한지 확인한다.
		validate(entity);
		//넘겨받은 엔티티 id를 이용해 TodoEntity를 가져온다. 존재하지 않는 엔티티는 업데이트할 수 없기 때문이다.
		final Optional<TodoEntity> original = repository.findById(entity.getId());

		//람다 사용하지 않은 경우
		// if(original.isPresent()){
		// 	//반환된 TodoEntity가 존재하면 값을 새 entity 값으로 덮어 씌운다.
		// 	final TodoEntity todo = original.get();
		// 	todo.setTitle(entity.getTitle());
		// 	todo.setDone(entity.isDone());
		// 	//데이터베이스에 새 값을 저장한다.
		// 	repository.save(todo);
		// }

		original.ifPresent(todo -> {
			//반환된 TodoEntity가 존재하면 값을 새 entity 값으로 덮어 씌운다.
			todo.setTitle(entity.getTitle());
			todo.setDone(entity.isDone());
			//데이터베이스에 새 값을 저장한다.
			repository.save(todo);
		});

		//2.3.2 Retrieve Todo 에서 만든 메서드를 이용해 사용자의 모든 Todo 리스트를 리턴한다.
		return retrieve(entity.getUserId());
	}

	public List<TodoEntity> delete(final TodoEntity entity) {
		//유효성검사
		validate(entity);
		try {
			//엔티티를 삭제한다
			repository.delete(entity);
		} catch (Exception e) {
			//exception 발생 시 id와 exception을 로깅한다
			log.error("error deleting entity", entity.getId(), e);

			//컨트롤러로 exception을 보낸다. 데이터베이스 내부 로직을 캡슐화하려면 e를 리턴하지 않고
			//새 exception 오브젝트를 리턴한다.
			throw new RuntimeException("error deleting entity" + entity.getId());
		}
		//새 Todo 리스트를 가져와 리턴한다.
		return retrieve(entity.getUserId());
	}
}
