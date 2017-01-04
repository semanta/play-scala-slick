const isValidElement = element => {
  return element.name && element.value;
};

const formToJSON = elements => [].reduce.call(elements, (data, element) => {
  if (element.type != "submit"){
  data[element.name] = element.value;
  }
  return data;
}, {});

const handleFormSubmit = event => {

  // Stop the form from submitting since we’re handling that with AJAX.
  event.preventDefault();

  // Call our function to get the form data.
  const data = formToJSON(form.elements);

  // Use `JSON.stringify()` to make the output valid, human-readable JSON.
  textContent = JSON.stringify(data, null, "  ");
  xhr = new XMLHttpRequest();
  var url = form.action;
  xhr.open("POST", url, true);
  xhr.setRequestHeader("Content-type", "application/json");
  xhr.onreadystatechange = function () {
      if (xhr.readyState == 4 && xhr.status == 200) {
          var json = JSON.parse(xhr.responseText);
          console.log(json.email + ", " + json.password)
      }
  }
  xhr.send(textContent);
};


const form = document.getElementsByClassName('userForm')[0];
form.addEventListener('submit', handleFormSubmit);

