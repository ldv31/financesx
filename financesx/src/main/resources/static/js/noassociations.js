// highlight le line selected (remve highlight off al  the other ones and add update the input cell )
$('#infoTable').on('click', 'tbody tr', function(event) {
  $(this).addClass('highlight').siblings().removeClass('highlight');
  
  //console.log($(this).find("td:eq(0)").text());
  var text = $(this).find("td:eq(7)").text();
  //console.log(text);
  //alert(text);
  
  // update input field
  //console.log($('#exampleFormControlInput1'));
  //console.log($('#labelInput'));
  //$("#labelInput").textContent = "toto";
  document.querySelector("#exampleFormControlInput1").value = text;
  //document.querySelector("#labelInput").textContent = text;
  
  
});
