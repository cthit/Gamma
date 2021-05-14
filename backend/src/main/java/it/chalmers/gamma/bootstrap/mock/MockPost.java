package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.internal.post.service.PostId;
import it.chalmers.gamma.internal.text.data.dto.TextDTO;

public record MockPost(PostId id,
                       TextDTO postName) { }
