package com.kienjd.controller;

import com.kienjd.configuration.Translator;
import com.kienjd.dto.response.PageResponse;
import com.kienjd.dto.response.ResponseData;
import com.kienjd.dto.response.ResponseError;
import com.kienjd.dto.response.UserDetailResponse;
import com.kienjd.service.UserService;
import com.kienjd.util.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.kienjd.dto.request.UserRequestDTO;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final String ERROR_MESSAGE = "errorMessage={}";

    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping("/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO request) {
        log.info("Request add user = {} {}", request.getFirstName(), request.getLastName());

        try {
            long userId = userService.saveUser(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"), userId);
        } catch (Exception e) {
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add user fail");
        }    }

    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{userId}")
    public ResponseData<Void> updateUser(@PathVariable @Min(1) long userId, @Valid @RequestBody UserRequestDTO request) {
        log.info("Request update userId={}", userId);

        try {
            userService.updateUser(userId, request);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.upd.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update user fail");
        }
    }

    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{userId}")
    public ResponseData<Void> updateStatus(@Min(1) @PathVariable long userId, @RequestParam UserStatus status) {
        log.info("Request change status, userId={}", userId);

        try {
            userService.changeStatus(userId, status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.change.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Change status fail");
        }    }

    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userId}")
    public ResponseData<Void> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") long userId) {
        log.info("Request delete userId={}", userId);

        try {
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), Translator.toLocale("user.del.success"));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete user fail");
        }    }

    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    @GetMapping("/{userId}")
    public ResponseData<UserDetailResponse> getUser(@PathVariable @Min(1) long userId) {
        log.info("Request get user detail, userId={}", userId);

        try {
            UserDetailResponse user = userService.getUser(userId);
            return new ResponseData<>(HttpStatus.OK.value(), "user", user);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }    }

    @Operation(summary = "Get list of users per pageNo",
            description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<PageResponse> getAllUser(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                           @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize) {
        log.info("Request get all of users");

        try {
            PageResponse<?> users = userService.getAllUsers(pageNo, pageSize);
            return new ResponseData<>(HttpStatus.OK.value(), "users", users);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users per pageNo",
            description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<?> getAllUsers(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                       @Min(10) @RequestParam(defaultValue = "20", required = false) int pageSize,
                                       @RequestParam(required = false) String sortBy) {
        log.info("Request get all of users");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.getAllUsersWithSortBy(pageNo, pageSize, sortBy));
    }

    @Operation(summary = "Get list of users with sort by multiple columns",
            description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ResponseData<?> getAllUsersWithSortByMultipleColumns(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                @RequestParam(required = false) String... sorts) {
        log.info("Request get all of users with sort by multiple columns");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.getAllUsersWithSortByMultipleColumns(pageNo, pageSize, sorts));
    }

    @Operation(summary = "Get list of users and search with paging and sorting by customize query",
            description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/list-user-and-search-with-paging-and-sorting")
    public ResponseData<?> getAllUsersAndSearchWithPagingAndSorting(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                                    @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                                    @RequestParam(required = false) String search,
                                                                    @RequestParam(required = false) String sortBy) {
        log.info("Request get list of users and search with paging and sorting");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.getAllUsersAndSearchWithPagingAndSorting(pageNo, pageSize, search, sortBy));
    }

    @Operation(summary = "Advance search query by criteria",
            description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/advance-search-with-criteria")
    public ResponseData<?> advanceSearchWithCriteria(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                     @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                     @RequestParam(required = false) String sortBy,
                                                     @RequestParam(required = false) String address,
                                                     @RequestParam(defaultValue = "") String... search) {
        log.info("Request advance search query by criteria");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.advanceSearchWithCriteria(pageNo, pageSize, sortBy, address, search));
    }
}
