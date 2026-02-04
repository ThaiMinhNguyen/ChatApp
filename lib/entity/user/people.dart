import 'package:equatable/equatable.dart';
import 'package:flutter_study/entity/user/user.dart';

class People extends Equatable {
  final User user;
  final bool isFriend;
  final bool isRequestSent;
  final bool isRequestReceived;

  const People({
    required this.user,
    this.isFriend = false,
    this.isRequestSent = false,
    this.isRequestReceived = false,
  });

  const People.empty()
      : user = const User.empty(),
        isFriend = false,
        isRequestSent = false,
        isRequestReceived = false;

  @override
  List<Object?> get props => [
    user,
    isFriend,
    isRequestSent,
    isRequestReceived,
  ];
}

sealed class FriendListItem extends Equatable {
  const FriendListItem();

  @override
  List<Object?> get props => [];
}


class PeopleHeader extends FriendListItem {
  final String title;

  const PeopleHeader(this.title);

  @override
  List<Object?> get props => [title];
}

class PersonItem extends FriendListItem {
  final People people;

  const PersonItem(this.people);

  @override
  List<Object?> get props => [people];
}


