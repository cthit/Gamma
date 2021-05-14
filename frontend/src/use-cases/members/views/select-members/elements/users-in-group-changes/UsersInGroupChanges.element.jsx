import * as _ from "lodash";
import React from "react";

import {
    DigitDesign,
    DigitText,
    useDigitTranslations
} from "@cthit/react-digit-components";

import translations from "./UsersInGroupChanges.element.translations";
import MemberAddition from "./sub-elements/member-addition";
import MemberCurrent from "./sub-elements/member-current";
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

const UsersInGroupChanges = ({ currentMembers, selectedMembers }) => {
    const [text] = useDigitTranslations(translations);

    return (
        <DigitDesign.Card margin={"0"} flex={"1"} alignSelf={"stretch"}>
            <DigitDesign.CardBody>
                <DigitText.Title text={text.Current} />
                {currentMembers.map(member => (
                    <MemberCurrent key={member.id} member={member} />
                ))}
                <DigitText.Title text={text.Additions} />
                {findAdditions(currentMembers, selectedMembers).map(member => (
                    <MemberAddition key={member.id} member={member} />
                ))}
                <DigitText.Title text={text.Deletions} />
                {findDeletions(currentMembers, selectedMembers).map(member => (
                    <MemberDeletion key={member.id} member={member} />
                ))}
            </DigitDesign.CardBody>
        </DigitDesign.Card>
    );
};

UsersInGroupChanges.defaultProps = {
    currentMembers: [],
    selectedMembers: []
};

export default UsersInGroupChanges;
