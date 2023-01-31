import { useContext, useMemo } from "react";

import {
    DigitDesign,
    DigitButton,
    useDigitTranslations,
    DigitText,
    DigitLayout,
    useDigitDialog
} from "@cthit/react-digit-components";

import { deleteApproval } from "../../../../../api/approval/delete.approval.api";

import GammaUserContext from "../../../../../common/context/GammaUser.context";

import translations from "./Approval.translations.component.json";

const Approval = ({
    clientUid,
    name,
    description,
    scopes = [],
    onApprovalDeleted
}) => {
    const [text] = useDigitTranslations(translations);
    const [openDialog] = useDigitDialog();
    const [user] = useContext(GammaUserContext);

    const hasEmail = useMemo(() => scopes.includes("EMAIL"), [scopes]);

    return (
        <DigitDesign.Card size={{ height: "auto" }}>
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={name} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardBody>
                <DigitLayout.Column>
                    <DigitText.Text text={description} />
                    <DigitDesign.Divider />
                    <DigitText.Text
                        text={text.InformationShared + name + ":"}
                    />
                    <DigitText.Text text={"- " + text.FirstAndLastName} />
                    {hasEmail && <DigitText.Text text={"- " + text.Email} />}
                    <DigitText.Text text={"- " + text.Nick} />
                    <DigitText.Text text={"- " + text.PreferredLanguage} />
                    <DigitText.Text text={"- " + text.Cid} />
                    <DigitText.Text text={"- " + text.Authorities} />
                    <DigitText.Text
                        text={"- " + text.GroupsThatYouAreApartOf}
                    />
                </DigitLayout.Column>
            </DigitDesign.CardBody>
            <DigitDesign.CardButtons leftRight>
                <DigitButton
                    onClick={() =>
                        openDialog({
                            title: text.AreYouSureRetractApproval,
                            description:
                                text.RetractApprovalDialogDescription + user.id,
                            confirmButtonText: text.Delete,
                            cancelButtonText: text.Cancel,
                            onConfirm: () => {
                                deleteApproval(clientUid);
                                onApprovalDeleted();
                            }
                        })
                    }
                    text={text.RetractApproval}
                    outlined
                />
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    );
};

export default Approval;
