package by.forwardsystem.utils;

import by.forwardsystem.dto.DatabaseConnectionInformation;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantsHolder {
    @Getter
    @Setter
    private static String botToken;

    @Getter
    @Setter
    private static DatabaseConnectionInformation connectionInformation;
}
