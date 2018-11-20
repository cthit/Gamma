import React from "react";
import {
    Fill,
    VerticalFill,
    Spacing,
    Hide,
    Size
} from "../../../../../common-ui/layout";
import { Text } from "../../../../../common-ui/text";
import GammaIconButton from "../../../../elements/gamma-icon-button";
import { Edit, Delete, Save, Cancel } from "@material-ui/icons";
import GammaTooltip from "../../../../elements/gamma-tooltip";
import {
    DigitTranslations,
    DigitTextField
} from "@cthit/react-digit-components";

import translations from "./EditableWebsite.view.translations.json";

import {
    CustomVerticalFill,
    CustomVerticalFillRightAlign
} from "./EditableWebsite.view.styles";

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
                                <GammaTooltip text={text.Edit}>
                                    <GammaIconButton
                                        component={Edit}
                                        onClick={() =>
                                            this.setState({
                                                editing: true
                                            })
                                        }
                                    />
                                </GammaTooltip>
                            </Hide>
                            <Hide hidden={!editing}>
                                <GammaTooltip text={text.Cancel}>
                                    <GammaIconButton
                                        component={Cancel}
                                        onClick={() =>
                                            this.setState({
                                                editing: false,
                                                newUrl: url
                                            })
                                        }
                                    />
                                </GammaTooltip>
                                <GammaTooltip text={text.Save}>
                                    <GammaIconButton
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
                                </GammaTooltip>
                            </Hide>
                            <div>
                                <GammaTooltip text={text.Delete}>
                                    <GammaIconButton
                                        component={Delete}
                                        onClick={() => onDelete()}
                                    />
                                </GammaTooltip>
                            </div>
                        </CustomVerticalFillRightAlign>
                    </CustomVerticalFill>
                )}
            />
        );
    }
}

export default EditableWebsite;
