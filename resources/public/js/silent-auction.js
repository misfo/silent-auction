$(function() {
  $('#create-item').click(function() {
    $('#modal').modal({});
  });

  $('.modal .save').click(function() {
    $(this).parents('.modal').find('form')[0].submit();
  });
});
