package com.example.minibankaccount.exeption;

import com.example.minibankaccount.payload.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class RestControllerExceptionHandler {

   @ExceptionHandler({RuntimeException.class})
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ResponseEntity<?> runtimeExeption(RuntimeException e){
       return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
   }

   @ExceptionHandler({NullPointerException.class})
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ResponseEntity<?> nullpointerExeption(NullPointerException e){
       return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
   }

   @ExceptionHandler({MissingServletRequestPartException.class})
   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
   public ResponseEntity<?> missingServletRequestParthandler(MissingServletRequestPartException e){
       return new ResponseEntity<>(new ApiResponse("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() , e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
   }

   @ExceptionHandler({AccessDeniedException.class})
   @ResponseStatus(HttpStatus.UNAUTHORIZED)
   public ResponseEntity<?> accessDeniedExceptionHandler(AccessDeniedException e){
       return new ResponseEntity<>(new ApiResponse("error",HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), e.getMessage()), HttpStatus.UNAUTHORIZED);
   }
//
//   @ExceptionHandler({MailSendException.class})
//   @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//   public ResponseEntity<?> mailSendExceptionHandler(MailSendException e){
//       return new ResponseEntity<>(new ApiResponse("error", HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() , e.getMostSpecificCause().getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
//   }


   @ExceptionHandler({DataIntegrityViolationException.class})
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ResponseEntity<?> sqlIntegrityConstraintViolationExceptionHandler(DataIntegrityViolationException e){
       return new ResponseEntity<>(new ApiResponse("error", HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT.getReasonPhrase() , e.getCause().getMessage()), HttpStatus.CONFLICT);
   }

   @ExceptionHandler({ResourceNotFoundException.class})
   @ResponseStatus(HttpStatus.NOT_FOUND)
   public ResponseEntity<?> responseResourceNotFoundExeptionHandler(ResourceNotFoundException e){
       return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
   }

//   @ExceptionHandler({FileStorageException.class})
//   @ResponseStatus(HttpStatus.BAD_REQUEST)
//   public ResponseEntity<?> fileStorageExceptionHandler(FileStorageException e){
//       return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
//   }
//
//   @ExceptionHandler({MyFileNotFoundException.class})
//   @ResponseStatus(HttpStatus.NOT_FOUND)
//   public ResponseEntity<?> myFileStorageNotFoundHandler(MyFileNotFoundException e){
//       return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()), HttpStatus.NOT_FOUND);
//   }
//
//   @ExceptionHandler({UserAlreadyReactedExeption.class})
//   @ResponseStatus(HttpStatus.CONFLICT)
//   public ResponseEntity<?> userAlreadyReactedExceptionHandler(UserAlreadyReactedExeption e){
//       return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.CONFLICT.getReasonPhrase(), e.getMessage()), HttpStatus.CONFLICT);
//   }

   @ExceptionHandler({MissingServletRequestParameterException.class})
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ResponseEntity<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e){
       return new ResponseEntity<>(new ApiResponse("error", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
   }

   @ExceptionHandler({AppException.class})
   @ResponseStatus(HttpStatus.BAD_REQUEST)
   public ResponseEntity<?> appExceptionHandler(AppException e){
       return new ResponseEntity<>(new ApiResponse("error",HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()), HttpStatus.BAD_REQUEST);
   }

//   @ExceptionHandler({MethodArgumentNotValidException.class})
//   @ResponseBody
//   @ResponseStatus(HttpStatus.BAD_REQUEST)
//   public ResponseEntity<?> resolveException(MethodArgumentNotValidException ex){
//       List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
//       List<String> messages = new ArrayList<>();
//       for (FieldError error : fieldErrors){
//           messages.add(error.getField() + " - " + error.getDefaultMessage());
//       }
//       return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
//   }
//
//   @ExceptionHandler({MethodArgumentTypeMismatchException.class})
//   @ResponseBody
//   @ResponseStatus(HttpStatus.BAD_REQUEST)
//   public ResponseEntity<?> resolveException(MethodArgumentTypeMismatchException ex){
//       String message = "Parameter '" + ex.getParameter().getParameterName() + "' must be '" + Objects.requireNonNull(ex.getRequiredType()).getSimpleName() + "'";
//       List<String> messages = new ArrayList<>();
//       messages.add(message);
//       return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
//   }
//
//   @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
//   @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
//   @ResponseBody
//   public ResponseEntity<?> resolveException(HttpRequestMethodNotSupportedException ex){
//       String message = "Request method '" + ex.getMethod() +"' not supported. List of all supported methods - " + ex.getSupportedHttpMethods();
//       List<String> messages = new ArrayList<>();
//       messages.add(message);
//       return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), HttpStatus.METHOD_NOT_ALLOWED.value()), HttpStatus.METHOD_NOT_ALLOWED);
//   }
//
//   @ExceptionHandler({HttpMessageNotReadableException.class})
//   @ResponseBody
//   @ResponseStatus(HttpStatus.BAD_REQUEST)
//   public ResponseEntity<?> resolveException(HttpMessageNotReadableException ex){
//       String message = "Please provide Request Body in valid JSON format";
//       List<String> messages = new ArrayList<>();
//       messages.add(message);
//       return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
//   }
}
