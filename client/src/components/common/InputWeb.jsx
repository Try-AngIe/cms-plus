const InputWeb = ({
  id,
  label,
  required,
  type = 'text',
  placeholder,
  classContainer = '',
  classLabel = '',
  classInput = '',
}) => {
  return (
    <div className={`${classContainer}`}>
      <label
        className={`${classLabel} block text-text_black text-15 font-700 mb-2 ml-2 ${required ? "before:mr-1 before:text-red-500 before:content-['*']" : ''}`}
        htmlFor={id}>
        {label}
      </label>
      <input
        className={`${classInput} placeholder:text-text_grey text-black border border-ipt_border focus:border-mint focus:outline-none focus:ring-mint focus:ring-1 placeholder:text-xs text-sm p-4  rounded-lg w-full`}
        id={id}
        type={type}
        placeholder={placeholder}
      />
    </div>
  );
};

export default InputWeb;
