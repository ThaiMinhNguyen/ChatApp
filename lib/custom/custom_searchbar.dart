import 'package:flutter/material.dart';

class CustomSearchBar extends StatefulWidget {
  final ValueChanged<String>? onChanged;
  final String? hintText;
  const CustomSearchBar({super.key, this.onChanged, this.hintText});

  @override
  State<CustomSearchBar> createState() => _CustomSearchBarState();
}

class _CustomSearchBarState extends State<CustomSearchBar> {
  final _focusNode = FocusNode();
  bool _isFocused = false;
  final _controller = TextEditingController();


  @override
  void initState() {
    super.initState();
    _focusNode.addListener(() {
      setState(() {
        _isFocused = _focusNode.hasFocus;
      });
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 250),
            curve: Curves.easeOut,
            child: SearchBar(
              controller: _controller,
              focusNode: _focusNode,
              hintText: widget.hintText ?? 'Search...',
              leading: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Image.asset('assets/icons/ic_search.png', height: 18,),
              ),
              onTap: () {
                _focusNode.requestFocus();
              },
              onChanged: widget.onChanged,
              backgroundColor: WidgetStatePropertyAll(Color(0xffFFFFFF)),
            ),
          ),
        ),

        AnimatedSwitcher(
          duration: const Duration(milliseconds: 200),
          child: _isFocused
              ? TextButton(
            key: const ValueKey('cancel'),
            onPressed: () {
              _focusNode.unfocus();
            },
            child: const Text(
                'Huỷ',
              style: TextStyle(
                color: Colors.white,
                fontSize: 16,
              ),
            ),
          )
              : const SizedBox.shrink(
            key: ValueKey('empty'),
          ),
        ),
      ],
    );
  }
}
