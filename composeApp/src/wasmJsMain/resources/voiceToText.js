let recognition = null;
let isListening = false;

let recognitionCallback = null;
let listeningCallback = null;
let microphoneErrorCallback = null;

function initRecognition() {
  if (!recognition) {
    recognition = new webkitSpeechRecognition();
    recognition.continuous = false;
    recognition.interimResults = false;

    recognition.onresult = function(event) {
      const result = event.results[0][0].transcript;
      if (recognitionCallback && typeof recognitionCallback === 'function') {
        recognitionCallback(result);
      }
    };

    recognition.onerror = function(event) {
      console.error('Ошибка при распознавании речи:', event.error);
      let errorMessage = 'Ошибка распознавания речи';
      if (event.error === 'not-allowed') {
        errorMessage = 'Микрофон не разрешен. Пожалуйста, проверьте настройки браузера.';
        console.error(errorMessage);
      }
       if (microphoneErrorCallback && typeof microphoneErrorCallback === 'function') {
            microphoneErrorCallback(errorMessage);
        }
    };

    recognition.onstart = function() {
      isListening = true;
      if (listeningCallback && typeof listeningCallback === 'function') {
        listeningCallback(isListening);
      }
    };

    recognition.onend = function() {
      isListening = false;
      if (listeningCallback && typeof listeningCallback === 'function') {
        listeningCallback(isListening);
      }
    };
  }
}

window.setRecognitionCallback = function(callback) {
  recognitionCallback = callback;
};

window.setListeningCallback = function(callback) {
  listeningCallback = callback;
};

window.setMicrophoneErrorCallback = function(callback) {
  microphoneErrorCallback = callback;
};

window.startListening = function() {
  navigator.mediaDevices.getUserMedia({ audio: true })
    .then(function(stream) {
      initRecognition();
      recognition.start();
      stream.getTracks().forEach(track => track.stop());
    })
    .catch(function(err) {
      console.error('Ошибка доступа к микрофону:', err);
      let errorMessage = 'Ошибка микрофона';
      if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
        errorMessage = 'Доступ к микрофону запрещен пользователем. Пожалуйста, проверьте настройки браузера.';
        console.error(errorMessage);
      } else if (err.name === 'NotFoundError' || err.name === 'DevicesNotFoundError') {
        errorMessage = 'Микрофон не найден.';
        console.error(errorMessage);
      } else {
        errorMessage = 'Неизвестная ошибка микрофона: ' + err.name;
        console.error(errorMessage);
      }
      if (microphoneErrorCallback && typeof microphoneErrorCallback === 'function') {
        microphoneErrorCallback(errorMessage);
      }
    });
};

window.stopListening = function() {
  if (recognition) {
    recognition.stop();
  } else {
    console.error('Recognition не был инициализирован.');
  }
};