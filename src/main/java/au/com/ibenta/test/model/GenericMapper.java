package au.com.ibenta.test.model;

import java.util.function.Function;

import com.googlecode.jmapper.JMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GenericMapper<D, S> {

	private final JMapper<? extends S, ? super D> sourceToDestinationMapper;

	private final JMapper<? extends D, ? super S> destinationToSourceMapper;

	public GenericMapper(Class<D> destination, Class<S> source) {

		sourceToDestinationMapper = new JMapper<>(source, destination);
		destinationToSourceMapper = new JMapper<>(destination, source);

	}

	public D to(S given) {
		return destinationToSourceMapper.getDestination(given);
	}

	public S from(D given) {
		return sourceToDestinationMapper.getDestination(given);
	}

	public Function<S, Mono<D>> ofMonoTo() {
		return given -> Mono.just(to(given));
	}

	public Function<D, Mono<S>> ofMonoFrom() {
		return given -> Mono.just(from(given));
	}

	public Function<S, Flux<D>> ofFluxTo() {
		return given -> Flux.just(to(given));
	}

	public Function<D, Flux<S>> ofFluxFrom() {
		return given -> Flux.just(from(given));
	}

}
