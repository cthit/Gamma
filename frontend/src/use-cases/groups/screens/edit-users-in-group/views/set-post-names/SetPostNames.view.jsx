import React from "react";

import { DigitDesign } from "@cthit/react-digit-components";
import CreatePost from "./sub-views/create-post";
import * as _ from "lodash";

const SetPostNames = ({ posts, currentMembers, members }) => (
    <DigitDesign.Card>
        <DigitDesign.CardBody>
            {members.map(member => (
                <CreatePost
                    posts={posts}
                    key={member.id}
                    member={member}
                    previousMemberData={_.find(currentMembers, {
                        id: member.id
                    })}
                />
            ))}
        </DigitDesign.CardBody>
    </DigitDesign.Card>
);

export default SetPostNames;
