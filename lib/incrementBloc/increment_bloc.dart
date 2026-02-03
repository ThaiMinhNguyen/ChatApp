import 'package:bloc/bloc.dart';
import 'package:equatable/equatable.dart';

part 'increment_state.dart';
part 'increment_event.dart';

class IncrementBloc extends Bloc<CounterEvent, IncrementState> {
  IncrementBloc() : super(IncrementInitial()){
    on<Increment>((event, emit) async {
      emit(state.copyWith(isLoading: true));
      await Future.delayed(const Duration(seconds: 1));
      emit(state.copyWith(count: state.count + 1, isLoading: false));
    });

    on<Decrement>((event, emit) async {
      emit(state.copyWith(isLoading: true));
      await Future.delayed(const Duration(seconds: 1));
      emit(state.copyWith(count: state.count - 1, isLoading: false));
    });

    on<Reset>((event, emit) async {
      emit(state.copyWith(isLoading: true));
      await Future.delayed(const Duration(seconds: 1));
      emit(state.copyWith(count: 0, isLoading: false));
    });
  }
}


