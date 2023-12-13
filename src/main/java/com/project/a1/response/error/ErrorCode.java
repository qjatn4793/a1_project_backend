package com.project.a1.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* Common */
    RESOURCE_NOT_FOUND(-200, "resource not found"),
    INVALID_REQUEST(-300, "invalid request"),
    INVALID_STATE(-310, "invalid state"),
    INVALID_CONFIG(-311, "Invalid getConfig"),
    FALLBACK_INVALID_DIALOG_ID(-401, "Invalid StatDialog Id"),

    COMMON_UNKNOWN(1001, "Unknown error occurred."),
    COMMON_INVALID_SERVICE_ID(1002, "Invalid service ID."),
    COMMON_JSON_EXCEPTION(1003, "Json parse exception"),

    /* Auth */
    AUTH_UNAUTHORIZED(2001, "Unauthorized error occurred."),
    AUTH_NOT_EXIST_USER(2002, "Not exist user."),

    /* Bot */
    BOT_NOT_EXIST(3001, "Not exist bot."),
    BOT_SNAPSHOT_NOT_EXIST(3002, "Not exist snapshot."),
    BOT_MAPPING_BOT_GROUP_EXIST(3003, "BotGroup mapping is exist."),
    BOT_INVALID_SNAPSHOT_STATUS(3004, "Invalid snapshot status."),
    BOT_DIFFERENT_SNAPSHOT_STATUS(3005, "Snapshots are in a different state."),

    /* BotGroup */
    BOT_GROUP_NOT_EXIST(4001, "Not exist bot group."),
    BOT_GROUP_ALREADY_EXIST_TRIGGER(4002, "Already exist trigger CALL && SYSTEM type."),
    BOT_GROUP_ALREADY_EXIST_INTENT(4003, "Already exist intent."),
    BOT_GROUP_PHASE_NOT_EXIST(4004, "BotGroupPhase not exist."),
    BOT_GROUP_STAGING_NOT_EXIST(4005, "Staging bot group is not exist."),
    BOT_GROUP_ATTRIBUTE_NOT_EXIST(4006, "BotGroup attribute is not exist."),
    BOT_GROUP_FREEZED(4007, "Freezed bot group."),
    BOT_GROUP_START_BOT_NOT_EXIST(4008, "Not exist startBot."),
    BOT_GROUP_PHASE_TOO_MANY(4009, "Too many search result of BotGroupPhase."),
    BOT_GROUP_ALREADY_EXIST_TARGET_ID(4010, "Already exist Target ID."),
    BOT_GROUP_ALREADY_EXIST_TARGET_NAME(4011, "Already exist Target Name."),
    BOT_GROUP_ALREADY_EXIST_BOT_GROUP_NAME(4012, "Already exist BotGroup Name."),
    BOT_GROUP_MAX_BOT_GROUP_INTENT_EXCEED(4013, "Max BotGroup Intent is exceeded."),

    /* Job */
    JOB_NOT_EXIST(5001, "Not exist job."),
    JOB_ALREADY_EXIST(5002, "Already exist job."),

    /* BotGroupJobMapping */
    BOT_GROUP_JOB_MAPPING_NOT_EXIST(6001, "Not exist bot group&job mapping."),
    BOT_GROUP_JOB_MAPPING_ALREADY_EXIST(6002, "Job already mapped to bot group."),
    BOT_GROUP_JOB_MAPPING_INVALID_CALLPOINT_RANGE(6003, "Invalid range of callpoint."),
    BOT_GROUP_JOB_MAPPING_CALLPOINT_REQUIRED(6004, "Callpoint is required."),
    BOT_GROUP_JOB_MAPPING_REQUIRED_NUMBER_FORMAT(6005, "Callpoint is required number format."),
    BOT_GROUP_JOB_MAPPING_INVALID_CALLPOINT_LENGTH(6006, "Callpoint length is different."),
    BOT_GROUP_JOB_MAPPING_DUPLICATE_CALLPOINT_EXISTS(6007, "Job with duplicate callpoint exists."),
    BOT_GROUP_JOB_MAPPING_CALLPOINT_END_LESSER_THAN_CALLPOINT_START(6008, "Callpoint-end is lesser than Callpoint-start."),


    /* Deploy */
    DEPLOY_NOT_EXIST(7001, "Not exist deploy."),
    DEPLOY_STATUS_IS_NOT_RESERVE(7002, "DeployStatus is not RESERVE."),
    DEPLOY_STATUS_INVALID(7003, "DeployStatus is RESERVE or REQUEST."),
    DEPLOY_ALREADY_PROGRESS(7004, "BotGroup deployment in progress."),
    DEPLOY_INVALID_LATEST_VERSION_NAME(7005, "Latest version name is invalid."),
    DEPLOY_ALREADY_COMPLETE(7006, "BotGroup deployment already complete."),

    /* JobEngine */
    JE_TRANSACTION_EXIST(8001, "Transaction is exist."),

    /* BotBuilder */
    BB_BOT_NOT_EXIST(9001, "Not exist bot."),
    BB_FAILED_TO_CHANGE_WORKFLOW(9002, "Workflow change request failed."),
    BB_FAILED_TO_GET_SNAPSHOT(9003, "Failed to get snapshot info."),



    ;

    private final Integer code;
    private final String message;
}
