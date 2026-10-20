package org.workswap.chat.dto;

import java.util.List;

import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.user.dto.ShortUserDTO;

public record ChatDetails(
    Long chatId,
    List<ShortUserDTO> interlocutors,
    ShortListingDTO listing
) {}
