part of 'increment_bloc.dart';

class IncrementState extends Equatable {
  final int count;
  final bool isLoading;

  const IncrementState({
    required this.count,
    this.isLoading = false,
  });

  IncrementState copyWith({
    int? count,
    bool? isLoading,
  }) {
    return IncrementState(
      count: count ?? this.count,
      isLoading: isLoading ?? this.isLoading,
    );
  }

  @override
  List<Object?> get props => [count, isLoading];
}

class IncrementInitial extends IncrementState {
  const IncrementInitial() : super(count: 0);
}