import React from "react";
import {
    DigitButton,
    DigitDesign,
    DigitList,
    DigitText,
    useDigitDialog,
    useDigitToast,
    useDigitTranslations
} from "@cthit/react-digit-components";
import translations from "./AuthorityLevelCard.element.translations";
import Delete from "@material-ui/icons/Delete";
import { deleteAuthorityLevel } from "../../../../../../api/authorities/delete.authoritites";

const AuthorityLevelCard = ({ authorityLevel, authorities = [] }) => {
    const [text, activeLanguage] = useDigitTranslations(translations);
    const [openDialog] = useDigitDialog();
    const [queueToast] = useDigitToast();

    console.log(authorities);

    return (
        <DigitDesign.Card size={{ minWidth: "280px", maxWidth: "400px" }}>
            <DigitDesign.CardHeader>
                <DigitDesign.CardTitle text={authorityLevel.authority} />
            </DigitDesign.CardHeader>
            <DigitDesign.CardBody>
                {authorities.length === 0 && (
                    <DigitText.Text alignCenter text={text.NoAuthorities} />
                )}
                {authorities.length > 0 && (
                    <DigitList
                        items={authorities.map(authority => ({
                            text:
                                authority.superGroup.prettyName +
                                " - " +
                                authority.post[activeLanguage],
                            actionIcon: Delete,
                            actionOnClick: () => {
                                console.log("delete");
                            }
                        }))}
                        onClick={null}
                        dense
                    />
                )}
            </DigitDesign.CardBody>
            <DigitDesign.CardButtons leftRight>
                <DigitButton
                    text={text.Delete}
                    onClick={() => {
                        openDialog({
                            title: text.AreYouSure,
                            description: text.AreYouSureDeleteAuthorityLevel,
                            confirmButtonText: text.ImSure,
                            cancelButtonText: text.Cancel,
                            onConfirm: () => {
                                deleteAuthorityLevel(authorityLevel.id)
                                    .then(() => {
                                        queueToast({
                                            text: text.DeleteSuccessful
                                        });
                                    })
                                    .catch(() => {
                                        queueToast({
                                            text: text.DeleteFailed
                                        });
                                    });
                            }
                        });
                    }}
                />
                <DigitDesign.Link to={"/authorities/edit/" + authorityLevel.id}>
                    <DigitButton text={text.AddToAuthority} outlined />
                </DigitDesign.Link>
            </DigitDesign.CardButtons>
        </DigitDesign.Card>
    );
};

export default AuthorityLevelCard;
