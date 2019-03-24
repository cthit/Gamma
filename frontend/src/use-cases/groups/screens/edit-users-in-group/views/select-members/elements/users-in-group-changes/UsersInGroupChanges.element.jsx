import React from "react";

import { DigitDesign, DigitText } from "@cthit/react-digit-components";
import MemberCurrent from "./sub-elements/member-current";
import * as _ from "lodash";
import MemberAddition from "./sub-elements/member-addition";
import MemberDeletion from "./sub-elements/member-deletion";

function findAdditions(currentMembers, selectedMembers) {
    return selectedMembers.filter(
        member => _.find(currentMembers, { id: member.id }) == null
    );
}

function findDeletions(currentMembers, selectedMembers) {
    return currentMembers.filter(
        member => _.find(selectedMembers, { id: member.id }) == null
    );
}

const UsersInGroupChanges = ({ currentMembers, selectedMembers }) => (
    <DigitDesign.Card absWidth="280px">
        <DigitDesign.CardBody>
            <DigitText.Title text="Nuvarande" />
            {currentMembers.map(member => (
                <MemberCurrent key={member.id} member={member} />
            ))}
            <DigitText.Title text="Tillägg" />
            {findAdditions(currentMembers, selectedMembers).map(member => (
                <MemberAddition key={member.id} member={member} />
            ))}
            <DigitText.Title text="Borttaggningar" />
            {findDeletions(currentMembers, selectedMembers).map(member => (
                <MemberDeletion key={member.id} member={member} />
            ))}
        </DigitDesign.CardBody>
    </DigitDesign.Card>
);

UsersInGroupChanges.defaultProps = {
    currentMembers: [],
    selectedMembers: []
};

export default UsersInGroupChanges;
