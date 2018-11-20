import {
    DigitIconButton,
    DigitTextField,
    DigitTranslations,
    DigitTooltip
} from "@cthit/react-digit-components";
import { Cancel, Delete, Edit, Save } from "@material-ui/icons";
import React from "react";
import {
    Hide,
    Size,
    Spacing,
    VerticalFill
} from "../../../../../common-ui/layout";
import { Text } from "../../../../../common-ui/text";
import {
    CustomVerticalFill,
    CustomVerticalFillRightAlign
} from "./EditableWebsite.view.styles";
import translations from "./EditableWebsite.view.translations.json";

class EditableWebsite extends React.Component {
    state = {
        editing: false,
        newUrl: this.props.url
    };
    render() {
        const { prettyWebsite, url, website, onChange, onDelete } = this.props;
        const { editing, newUrl } = this.state;
        return (
            <DigitTranslations
                translations={translations}
                uniquePath="Common.Views.EditWebsites.SubView.EditableWebsite"
                render={text => (
                    <CustomVerticalFill>
                        <VerticalFill>
                            <Size minWidth="150px">
                                <Text text={prettyWebsite} />
                            </Size>
                            <Spacing />
                            <Hide hidden={editing}>
                                <Text text={url} />
                            </Hide>
                            <Hide hidden={!editing}>
                                <DigitTextField
                                    upperLabel={text.Url}
                                    value={newUrl}
                                    onChange={e => {
                                        this.setState({
                                            newUrl: e.target.value
                                        });
                                    }}
                                />
                            </Hide>
                        </VerticalFill>
                        <CustomVerticalFillRightAlign>
                            <Hide hidden={editing}>
                                <DigitTooltip text={text.Edit}>
                                    <DigitIconButton
                                        component={Edit}
                                        onClick={() =>
                                            this.setState({
                                                editing: true
                                            })
                                        }
                                    />
                                </DigitTooltip>
                            </Hide>
                            <Hide hidden={!editing}>
                                <DigitTooltip text={text.Cancel}>
                                    <DigitIconButton
                                        component={Cancel}
                                        onClick={() =>
                                            this.setState({
                                                editing: false,
                                                newUrl: url
                                            })
                                        }
                                    />
                                </DigitTooltip>
                                <DigitTooltip text={text.Save}>
                                    <DigitIconButton
                                        component={Save}
                                        onClick={() => {
                                            this.setState({
                                                editing: false
                                            });
                                            onChange({
                                                website: website,
                                                url: this.state.newUrl
                                            });
                                        }}
                                    />
                                </DigitTooltip>
                            </Hide>
                            <div>
                                <DigitTooltip text={text.Delete}>
                                    <DigitIconButton
                                        component={Delete}
                                        onClick={() => onDelete()}
                                    />
                                </DigitTooltip>
                            </div>
                        </CustomVerticalFillRightAlign>
                    </CustomVerticalFill>
                )}
            />
        );
    }
}

export default EditableWebsite;
