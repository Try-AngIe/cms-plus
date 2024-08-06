const TextArea = ({
  id,
  label,
  disabled,
  placeholder,
  classContainer = '',
  classLabel = '',
  classTextarea = '',
  ...props
}) => {
  return (
    <div className={`${classContainer} `}>
      <label
        className={`${classLabel} block text-text_black text-15 font-700 mb-2 ml-2 `}
        htmlFor={id}>
        {label}
      </label>
      <div className='relative '>
        <textarea
          className={`${classTextarea} placeholder:text-text_grey text-black border resize-none
                    ${disabled && 'bg-ipt_disa '} border-ipt_border focus:border-mint focus:outline-none 
                    focus:ring-mint focus:ring-1 placeholder:text-xs text-sm p-4  rounded-lg w-full`}
          id={id}
          disabled={disabled}
          placeholder={placeholder}
          {...props}
          maxLength={2000}
        />
      </div>
    </div>
  );
};

export default TextArea;
